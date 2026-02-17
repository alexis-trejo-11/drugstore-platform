package microservice.auth_service.config;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import libs_kernel.config.CustomGlobalExceptionHandler;

@RestControllerAdvice
@Order(-1) // Higher priority than shared kernel
public class GlobalExceptionHandler extends CustomGlobalExceptionHandler {

}
