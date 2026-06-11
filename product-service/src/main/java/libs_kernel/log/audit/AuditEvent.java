package libs_kernel.log.audit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuditEvent {
    private String eventID = UUID.randomUUID().toString();
    private String timeStamp = java.time.Instant.now().toString();
    private String serviceName;
    private String method;
    private String endpoint;
    private String operation;
    private String userID;
    private String clientIP;
    private String userAgent;
    private int statusCode;
    private long durationMs;
    private boolean success;
    private Map<String, Object> metadata = new HashMap<>();

    public AuditEvent() {
    }

    public AuditEvent(String eventID, String timeStamp, String serviceName, String method, String endpoint, String operation, String userID, String clientIP, String userAgent, int statusCode, long durationMs, boolean success, Map<String, Object> metadata) {
        this.eventID = eventID;
        this.timeStamp = timeStamp;
        this.serviceName = serviceName;
        this.method = method;
        this.endpoint = endpoint;
        this.operation = operation;
        this.userID = userID;
        this.clientIP = clientIP;
        this.userAgent = userAgent;
        this.statusCode = statusCode;
        this.durationMs = durationMs;
        this.success = success;
        this.metadata = metadata;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .eventID(this.eventID)
                .timeStamp(this.timeStamp)
                .serviceName(this.serviceName)
                .method(this.method)
                .endpoint(this.endpoint)
                .operation(this.operation)
                .userID(this.userID)
                .clientIP(this.clientIP)
                .userAgent(this.userAgent)
                .statusCode(this.statusCode)
                .durationMs(this.durationMs)
                .success(this.success)
                .metadata(this.metadata == null ? new HashMap<>() : new HashMap<>(this.metadata));
    }

    public static class Builder {
        private String eventID;
        private String timeStamp;
        private String serviceName;
        private String method;
        private String endpoint;
        private String operation;
        private String userID;
        private String clientIP;
        private String userAgent;
        private int statusCode;
        private long durationMs;
        private boolean success;
        private Map<String, Object> metadata;

        public Builder eventID(String eventID) {
            this.eventID = eventID;
            return this;
        }

        public Builder timeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder operation(String operation) {
            this.operation = operation;
            return this;
        }

        public Builder userID(String userID) {
            this.userID = userID;
            return this;
        }

        public Builder clientIP(String clientIP) {
            this.clientIP = clientIP;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder durationMs(long durationMs) {
            this.durationMs = durationMs;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public AuditEvent build() {
            String finalEventID = (this.eventID != null && !this.eventID.isEmpty()) ? this.eventID : UUID.randomUUID().toString();
            String finalTimeStamp = (this.timeStamp != null && !this.timeStamp.isEmpty()) ? this.timeStamp : java.time.Instant.now().toString();
            Map<String, Object> finalMetadata = (this.metadata != null) ? this.metadata : new HashMap<>();

            return new AuditEvent(
                    finalEventID,
                    finalTimeStamp,
                    this.serviceName,
                    this.method,
                    this.endpoint,
                    this.operation,
                    this.userID,
                    this.clientIP,
                    this.userAgent,
                    this.statusCode,
                    this.durationMs,
                    this.success,
                    finalMetadata
            );
        }
    }

}
