package microservice.product_service.Controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import microservice.product_service.Service.ValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/drugstore")
@Tag(name = "Validate Controller", description = "Controller Specially Created to Validate insert data of product-service in another services")
public class ValidateController {

    private final ValidateService validateService;

    @Autowired
    public ValidateController(ValidateService validateService) {
        this.validateService = validateService;
    }

    @GetMapping("products/validate/{productId}")
    public boolean validateProductById(@PathVariable Long productId) {
        return validateService.validateExisitingProduct(productId);
    }

    @GetMapping("suppliers/validate/{supplierId}")
    public boolean validateSupplierById(@PathVariable Long supplierId) {
        return validateService.validateExistingSupplier(supplierId);
    }
}
