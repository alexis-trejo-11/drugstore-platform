package io.github.alexisTrejo11.drugstore.products.adapter.in.web.annotations;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Tag(name = "Product Management", description = "Operations related to product creation, retrieval, update, and deletion")
public @interface ProductControllerTag {
}
