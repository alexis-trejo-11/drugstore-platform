package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierReturnDTO;
import microservice.product_service.Service.SupplierServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/products/suppliers")
public class SupplierController {

    private final SupplierServiceImpl supplierServiceImpl;

    @Autowired
    public SupplierController(SupplierServiceImpl supplierServiceImpl) {
        this.supplierServiceImpl = supplierServiceImpl;
    }

    @PostMapping
    public ResponseEntity<Void> insertSupplier(@RequestBody SupplierInsertDTO supplierInsertDTO) {
        supplierServiceImpl.insertSupplier(supplierInsertDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierReturnDTO> getSupplierById(@PathVariable Long id) {
        SupplierReturnDTO supplierReturnDTO = supplierServiceImpl.getSupplierById(id);
        return supplierReturnDTO != null ? ResponseEntity.ok().body(supplierReturnDTO) : ResponseEntity.notFound().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SupplierReturnDTO> getSupplierByName(@PathVariable String name) {
        SupplierReturnDTO supplierReturnDTO = supplierServiceImpl.getSupplierByName(name);
        return supplierReturnDTO != null ? ResponseEntity.ok().body(supplierReturnDTO) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<SupplierReturnDTO>> getAllSuppliers() {
        List<SupplierReturnDTO> supplierReturnDTOs = supplierServiceImpl.getAllSuppliers();
        return ResponseEntity.ok().body(supplierReturnDTOs);
    }

    @PutMapping
    public ResponseEntity<Void> updateSupplier(@RequestBody SupplierReturnDTO supplierDTO) {
        boolean success = supplierServiceImpl.updateSupplier(supplierDTO);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        boolean success = supplierServiceImpl.deleteSupplier(id);
        return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
