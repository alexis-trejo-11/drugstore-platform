package io.github.alexisTrejo11.drugstore.accounts.config.log;


import com.fasterxml.jackson.databind.ObjectMapper;
import libs_kernel.log.audit.AuditEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditLogger {
    private final ObjectMapper objectMapper;

    @Autowired
    public AuditLogger(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void logAuditEvent(AuditEvent auditEvent) {
        try {
            String eventJson = objectMapper.writeValueAsString(auditEvent);
            log.info("AUDIT EVENT: {}", eventJson);
        } catch (Exception e) {
            log.error("Failed to log audit event", e);
        }
    }

}