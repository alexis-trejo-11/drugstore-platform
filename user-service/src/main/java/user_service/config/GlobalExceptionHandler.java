package user_service.config;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import libs_kernel.config.CustomGlobalExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends CustomGlobalExceptionHandler {

}
