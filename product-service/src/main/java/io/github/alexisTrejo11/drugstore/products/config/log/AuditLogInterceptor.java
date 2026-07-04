package io.github.alexisTrejo11.drugstore.products.config.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import libs_kernel.log.audit.AuditEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AuditLogInterceptor implements HandlerInterceptor {
	protected static final Logger log = org.slf4j.LoggerFactory.getLogger(AuditLogInterceptor.class);
	protected final AuditLogger auditLogger;
	protected final String serviceName;
	protected static final ThreadLocal<Long> startTime = new ThreadLocal<>();

	protected final Set<String> excludedEndpoints = Set.of(
			"/actuator/health",
			"/actuator/info",
			"/actuator/metrics",
			"/favicon.ico",
			"/swagger-ui.html",
			"/v3/api-docs",
			"/webjars",
			"/swagger-resources"
	);

	@Autowired
	public AuditLogInterceptor(AuditLogger auditLogger, String serviceName) {
		this.auditLogger = auditLogger;
		this.serviceName = serviceName;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
	                         HttpServletResponse response,
	                         Object handler) {
		if (shouldSkipAudit(request)) {
			return true;
		}
		startTime.set(System.currentTimeMillis());
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
	                            HttpServletResponse response,
	                            Object handler,
	                            Exception ex) {
		if (shouldSkipAudit(request)) return;

		Long start = startTime.get();
		if (start == null) return;

		long duration = System.currentTimeMillis() - start;

		try {
			AuditEvent event = AuditEvent.builder()
					.serviceName(serviceName)
					.method(request.getMethod())
					.endpoint(sanitizeEndpoint(request.getRequestURI()))
					.operation(extractOperation(request))
					.userID(extractUserId(request))
					.clientIP(getClientIp(request))
					.userAgent(request.getHeader("User-Agent"))
					.statusCode(response.getStatus())
					.durationMs(duration)
					.success(ex == null && response.getStatus() < 400)
					.metadata(buildMetadata(request, ex))
					.build();

			auditLogger.logAuditEvent(event);

		} catch (Exception e) {
			log.warn("Error creating audit event", e);
		} finally {
			startTime.remove();
		}
	}

	protected boolean shouldSkipAudit(HttpServletRequest request) {
		String requestURI = request.getRequestURI();

		for (String excluded : excludedEndpoints) {
			if (requestURI.startsWith(excluded)) {
				return true;
			}
		}

		return "OPTIONS".equalsIgnoreCase(request.getMethod());
	}

	protected String sanitizeEndpoint(String endpoint) {
		if (endpoint == null) return "";

		return endpoint.replaceAll("/\\d+", "/{id}")
				.replaceAll("/[0-9a-fA-F-]{36}", "/{uuid}")
				.replaceAll("/[A-Z0-9]{10,}", "/{code}");
	}

	protected String extractOperation(HttpServletRequest request) {
		String method = request.getMethod().toUpperCase();
		String endpoint = request.getRequestURI();

		if (endpoint.contains("/products")) {
			return switch (method) {
				case "GET" -> endpoint.matches(".*/\\{id\\}$") ? "GET_PRODUCT" : "LIST_PRODUCTS";
				case "POST" -> "CREATE_PRODUCT";
				case "PUT", "PATCH" -> "UPDATE_PRODUCT";
				case "DELETE" -> "DELETE_PRODUCT";
				default -> method + "_PRODUCT";
			};
		}

		return method + "_" + endpoint.replace("/", "_")
				.replaceAll("[^a-zA-Z0-9_]", "")
				.toUpperCase();
	}

	protected String extractUserId(HttpServletRequest request) {
		String userIdHeader = request.getHeader("X-User-ID");
		if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
			return userIdHeader;
		}

		Object userIdAttr = request.getAttribute("userId");
		if (userIdAttr != null) {
			return userIdAttr.toString();
		}

		return "anonymous";
	}

	protected String getClientIp(HttpServletRequest request) {
		String[] ipHeaders = {
				"X-Forwarded-For",
				"X-Real-IP",
				"Proxy-Client-IP",
				"WL-Proxy-Client-IP",
				"HTTP_X_FORWARDED_FOR",
				"HTTP_X_FORWARDED",
				"HTTP_X_CLUSTER_CLIENT_IP",
				"HTTP_CLIENT_IP",
				"HTTP_FORWARDED_FOR",
				"HTTP_FORWARDED",
				"HTTP_VIA",
				"REMOTE_ADDR"
		};

		for (String header : ipHeaders) {
			String ip = request.getHeader(header);
			if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
				if (header.equals("X-Forwarded-For")) {
					return ip.split(",")[0].trim();
				}
				return ip;
			}
		}

		return request.getRemoteAddr();
	}

	protected Map<String, Object> buildMetadata(HttpServletRequest request, Exception ex) {
		Map<String, Object> metadata = new HashMap<>();

		metadata.put("queryString", request.getQueryString());
		metadata.put("contentType", request.getContentType());
		metadata.put("serverName", request.getServerName());
		metadata.put("serverPort", request.getServerPort());
		metadata.put("locale", request.getLocale().toString());

		if (ex != null) {
			metadata.put("errorType", ex.getClass().getSimpleName());
			metadata.put("errorMessage", ex.getMessage());
		}

		return metadata;
	}
}
