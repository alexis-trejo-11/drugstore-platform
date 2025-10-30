package libs_kernel.config;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    String key() default "";
    int maxRequests() default -1;
    int duration() default -1;
    TimeUnit durationUnit() default TimeUnit.SECONDS;
    RateLimitType type() default RateLimitType.IP_BASED;
}

