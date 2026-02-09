package microservice.product_service.app.adapter.in.web.annotations;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.*;

public class ProductApiParameters {

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  @Parameter(description = "Query parameters for product search and pagination")
  public @interface SearchQuery {
  }

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  @Parameter(description = "Unique identifier of the product (Max length 36 like UUID)", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
  public @interface ProductId {
  }
}
