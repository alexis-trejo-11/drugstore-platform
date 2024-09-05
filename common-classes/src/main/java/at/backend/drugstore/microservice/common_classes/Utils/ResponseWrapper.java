package at.backend.drugstore.microservice.common_classes.Utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonRootName(value = "ResponseWrapper")
public class ResponseWrapper<T> {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("data")
    private T data;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status_code")
    private int statusCode;

    public ResponseWrapper(boolean success, T data, String message, int statusCode) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.statusCode = statusCode;
    }

    public static <T> ResponseWrapper<T> created(String entity) {
        String createMsg = entity  + " Successfully Created";
        return new ResponseWrapper<>(true, null, createMsg, 201);
    }

    public static <T> ResponseWrapper<T> found(T data, String entity) {
        String foundMsg = entity  + " Data Successfully Fetched";
        return new ResponseWrapper<>(true, data, foundMsg, 200);
    }

    public static <T> ResponseWrapper<T> notFound(String entity, String filter) {
        String notFoundMsg = entity  + " With " + filter + " Not Found";
        return new ResponseWrapper<>(false, null, notFoundMsg, 404);
    }

    public static <T> ResponseWrapper<T> badRequest(String msg) {
        return new ResponseWrapper<>(false, null, msg, 404);
    }

    public static <T> ResponseWrapper<T> ok(String entity, String action) {
        String notFoundMsg = entity  + " Successfully " + action + "d";
        return new ResponseWrapper<>(false, null, notFoundMsg, 200);
    }

}

