package libs_kernel.documentation;

import io.swagger.v3.oas.annotations.media.Schema;
import libs_kernel.response.ErrorDetails;
import libs_kernel.response.ResponseWrapper;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Swagger-annotated version of ResponseWrapper for API documentation purposes only.
 * This class extends the original ResponseWrapper to add Swagger annotations without
 * modifying the existing production code.
 */
@Schema(description = "Standard API response wrapper")
public class SwaggerResponseWrapper<T> extends ResponseWrapper<T> {

    @Schema(
            description = "Indicates whether the operation was successful",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Override
    public boolean isSuccess() {
        return super.isSuccess();
    }

    @Schema(
            description = "Human-readable message describing the response",
            example = "Operation completed successfully",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Schema(
            description = "The actual response payload data",
            example = "{\"id\": \"123\", \"name\": \"John Doe\"}",
            nullable = true
    )
    @Override
    public T getData() {
        return super.getData();
    }

    @Schema(
            description = "Timestamp when the response was created",
            example = "2025-10-08T10:30:00.000Z",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "date-time"
    )
    @Override
    public LocalDateTime getTimestamp() {
        return super.getTimestamp();
    }

    @Schema(
            description = "Detailed error information for failed operations",
            implementation = SwaggerErrorDetails.class,
            nullable = true
    )
    @Override
    public ErrorDetails getErrorDetails() {
        return super.getErrorDetails();
    }
}

@Schema(description = "Detailed error information")
@Getter
@Setter
class SwaggerErrorDetails {

    @Schema(description = "Error code", example = "VALIDATION_ERROR")
    private String errorCode;

    @Schema(description = "Detailed error message", example = "Email format is invalid")
    private String errorMessage;

    @Schema(description = "Type of error", example = "BusinessException")
    private String errorType;

    @Schema(description = "Additional error details", nullable = true)
    private Map<String, Object> details;

}