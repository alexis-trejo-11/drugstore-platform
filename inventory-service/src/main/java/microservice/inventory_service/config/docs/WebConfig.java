package microservice.inventory_service.config.docs;

import libs_kernel.log.audit.AuditLogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuditLogInterceptor auditLogInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditLogInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );
    }
}