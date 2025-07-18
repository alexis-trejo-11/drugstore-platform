package microservice.product_service.app.shared;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

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


    public Map<String, String> errors;


    public ResponseWrapper(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static <T> ResponseWrapper<T> created(String entity) {
        String createMsg = entity  + " Successfully Created";
        return new ResponseWrapper<>(true, null, createMsg);
    }

    public static <T> ResponseWrapper<T> created(T data, String entity) {
        String createMsg = entity  + " Successfully Created";
        return new ResponseWrapper<>(true, data, createMsg);
    }

    public static <T> ResponseWrapper<T> update(String entity) {
        String updateMsg = entity  + " Successfully Update";
        return new ResponseWrapper<>(true, null, updateMsg);
    }

    public static <T> ResponseWrapper<T> update(T data, String entity) {
        String updateMsg = entity  + " Successfully Update";
        return new ResponseWrapper<>(true, data, updateMsg);
    }

    public static <T> ResponseWrapper<T> deleted(String entity) {
        String deleteMsg = entity  + " Successfully Deleted";
        return new ResponseWrapper<>(true, null, deleteMsg);
    }


    public static <T> ResponseWrapper<T> found(T data, String entity) {
        String foundMsg = entity  + " Data Successfully Fetched";
        return new ResponseWrapper<>(true, data, foundMsg);
    }

    public static <T> ResponseWrapper<T> ok(T data, String entity, String action) {
        String notFoundMsg = entity  + " Successfully " + action + "d";
        return new ResponseWrapper<>(true, data, notFoundMsg);
    }

    public static <T> ResponseWrapper<T> ok(String entity, String action) {
        String notFoundMsg = entity  + " Successfully " + action + "d";
        return new ResponseWrapper<>(true, null, notFoundMsg);
    }

    public static <T> ResponseWrapper<T> success(String msg) {
        return new ResponseWrapper<>(true, null, msg);
    }

    public static <T> ResponseWrapper<T> notFound(String entity) {
        String notFoundMsg = entity  + " " + "Not Found";
        return new ResponseWrapper<>(false, null, notFoundMsg);
    }

    public static <T> ResponseWrapper<T> badRequest(String msg) {
        return new ResponseWrapper<>(false, null, msg);
    }

    public static <T> ResponseWrapper<T> unauthorized(String msg) {
        return new ResponseWrapper<>(false, null, msg);
    }

    public static <T> ResponseWrapper<T> error(Map<String, String> errors) {
        var errorResponse = new ResponseWrapper<T>(false, null, "error");
        errorResponse.errors = errors;
        return errorResponse;
    }
}

