package microservice.ecommerce_cart_service.Utils;

import at.backend.drugstore.microservice.common_models.Utils.Result;

public class ControllerHelper {

    /**
     * Validate and parse a string ID into the specified type.
     *
     * @param stringId The string representation of the ID.
     * @param clazz    The class of the type to parse into.
     * @param <T>      The type to parse into.
     * @return A Result containing the parsed ID if successful, or an error message if validation/parsing failed.
     */
    public static <T> Result<T> validateAndParseId(String stringId, Class<T> clazz) {
        if (stringId == null || stringId.trim().isEmpty()) {
            return Result.error("Client Id can't be null or empty");
        }

        if (clazz == Long.class) {
            return Result.success(clazz.cast(Long.parseLong(stringId)));
        } else if (clazz == Integer.class) {
            return Result.success(clazz.cast(Integer.parseInt(stringId)));
        }

        return Result.error("Unsupported ID type");
    }
}


