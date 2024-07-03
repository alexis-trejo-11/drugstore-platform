package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierReturnDTO;
import microservice.product_service.Service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/products/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    public ResponseEntity<Void> insertSupplier(@RequestBody SupplierInsertDTO supplierInsertDTO) {
        supplierService.insertSupplier(supplierInsertDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierReturnDTO> getSupplierById(@PathVariable Long id) {
        SupplierReturnDTO supplierReturnDTO = supplierService.getSupplierById(id);
        return supplierReturnDTO != null ? ResponseEntity.ok().body(supplierReturnDTO) : ResponseEntity.notFound().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SupplierReturnDTO> getSupplierByName(@PathVariable String name) {
        SupplierReturnDTO supplierReturnDTO = supplierService.getSupplierByName(name);
        return supplierReturnDTO != null ? ResponseEntity.ok().body(supplierReturnDTO) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<SupplierReturnDTO>> getAllSuppliers() {
        List<SupplierReturnDTO> supplierReturnDTOs = supplierService.getAllSuppliers();
        return ResponseEntity.ok().body(supplierReturnDTOs);
    }

    @PutMapping
    public ResponseEntity<Void> updateSupplier(@RequestBody SupplierReturnDTO supplierDTO) {
        boolean success = supplierService.updateSupplier(supplierDTO);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        boolean success = supplierService.deleteSupplier(id);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
