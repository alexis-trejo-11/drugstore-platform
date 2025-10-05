package libs_kernel.response;

import lombok.Getter;

@Getter
public class Result<T> {
    private boolean isSuccess;
    private String message;
    private T data;
    private int statusCode;

    private Result() {
    }

    public static <U> Result<U> success(U data, String message, int statusCode) {
        Result<U> result = new Result<>();
        result.isSuccess = true;
        result.message = message;
        result.data = data;
        result.statusCode = statusCode;
        return result;
    }

    public static <U> Result<U> success(U data, String message) {
        Result<U> result = new Result<>();
        result.isSuccess = true;
        result.message = message;
        result.data = data;
        result.statusCode = 200;
        return result;
    }

    public static <U> Result<U> success(U data) {
        Result<U> result = new Result<>();
        result.isSuccess = true;
        result.message = "";
        result.data = data;
        result.statusCode = 200;
        return result;
    }

    public static <U> Result<U> success() {
        Result<U> result = new Result<>();
        result.isSuccess = true;
        result.message = "";
        result.data = null;
        result.statusCode = 200;
        return result;
    }

    public static <U> Result<U> error(String message, int statusCode) {
        Result<U> result = new Result<>();
        result.isSuccess = false;
        result.message = message;
        result.data = null;
        result.statusCode = statusCode;
        return result;
    }

}