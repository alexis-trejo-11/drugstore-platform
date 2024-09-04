package at.backend.drugstore.microservice.common_classes.Utils;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;

public class EntityMapper {

    public static <T, U> void mapNonNullProperties(T source, U target) {
        Method[] methods = source.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().startsWith("get")) {
                try {
                    Object value = method.invoke(source);
                    if (value != null) {
                        String fieldName = method.getName().substring(3);
                        Method setter = target.getClass().getMethod("set" + fieldName, method.getReturnType());
                        setter.invoke(target, value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to map non-null property " + e.toString(), e);
                }
            }
        }
    }
}

