package libs_kernel.log.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ApplicationLogger {

    public void logBusinessEvent(String eventType, String message, Object data) {
        log.info("BUSINESS_EVENT: type={}, message={}, data={}",
                eventType, message, data);
    }

    public void logPerformance(String operation, long durationMs) {
        log.info("PERFORMANCE: operation={}, durationMs={}", operation, durationMs);
    }

    public void logError(String context, Exception error, Map<String, Object> details) {
        log.error("ERROR: context={}, error={}, details={}",
                context, error.getMessage(), details);
    }
}