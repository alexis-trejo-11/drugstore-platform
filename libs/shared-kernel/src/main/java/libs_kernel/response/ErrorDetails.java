package libs_kernel.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDetails {
    private String errorCode;
    private String errorMessage;
    private String errorType;
    private Map<String, String> details;
}
