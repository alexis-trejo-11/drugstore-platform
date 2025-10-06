package microservice.order_service.config;

import libs_kernel.config.CustomGlobalExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends CustomGlobalExceptionHandler  {
}
