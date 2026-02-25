package io.github.alexisTrejo11.drugstore.address.config;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import libs_kernel.config.CustomGlobalExceptionHandler;

@RestControllerAdvice
@Order(0)
public class GlobalExceptionHandler extends CustomGlobalExceptionHandler {
}
