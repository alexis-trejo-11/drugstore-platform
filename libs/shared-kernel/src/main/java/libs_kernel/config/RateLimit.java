package libs_kernel.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    String key() default  "";
    int maxRequests() default  100;
    int duration() default  60;
    TimeUnit durationUnit() default TimeUnit.SECONDS;
}
