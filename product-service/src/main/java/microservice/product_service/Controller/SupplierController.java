package microservice.product_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierReturnDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import microservice.product_service.Service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping("admin/suppliers/add")
    public CompletableFuture<ResponseEntity<ResponseWrapper<SupplierReturnDTO>>> addSupplier(@RequestBody SupplierInsertDTO supplierDTO) {
        return supplierService.insertSupplier(supplierDTO)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        ResponseWrapper<SupplierReturnDTO> response = new ResponseWrapper<>(result.getData(), null, HttpStatus.CREATED);
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
                    } else {
                        ResponseWrapper<SupplierReturnDTO> response = new ResponseWrapper<>(null, result.getErrorMessage(), HttpStatus.BAD_REQUEST);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                })
                .exceptionally(ex -> {
                    ResponseWrapper<SupplierReturnDTO> response = new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.BAD_REQUEST);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                });
    }

    @GetMapping("suppliers/{id}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<SupplierReturnDTO>>> getSupplierById(@PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        ResponseWrapper<SupplierReturnDTO> response = new ResponseWrapper<>(result.getData(), null, HttpStatus.OK);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    } else {
                        ResponseWrapper<SupplierReturnDTO> response = new ResponseWrapper<>(null, result.getErrorMessage(), HttpStatus.NOT_FOUND);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                })
                .exceptionally(ex -> {
                    ResponseWrapper<SupplierReturnDTO> response = new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.BAD_REQUEST);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                });
    }

    @GetMapping("suppliers/name/{name}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<SupplierReturnDTO>>> getSupplierByName(@PathVariable String name) {
        return supplierService.getSupplierByName(name)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        ResponseWrapper<SupplierReturnDTO> response = new ResponseWrapper<>(result.getData(), null, HttpStatus.OK);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    } else {
                        ResponseWrapper<SupplierReturnDTO> response = new ResponseWrapper<>(null, result.getErrorMessage(), HttpStatus.NOT_FOUND);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                })
                .exceptionally(ex -> {
                    ResponseWrapper<SupplierReturnDTO> response = new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                });
    }

    @GetMapping("admin/suppliers/all")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<SupplierReturnDTO>>>> getAllSuppliers() {
        return supplierService.getAllSuppliers()
                .thenApply(list -> {
                    ResponseWrapper<List<SupplierReturnDTO>> response = new ResponseWrapper<>(list, null, HttpStatus.OK);
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                })
                .exceptionally(ex -> {
                    ResponseWrapper<List<SupplierReturnDTO>> response = new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                });
    }

    @PutMapping("admin/suppliers/update/{id}")
    public CompletableFuture<ResponseEntity<?>> updateSupplier(@PathVariable Long id, @RequestBody SupplierReturnDTO supplierDTO) {
        return supplierService.updateSupplier(id, supplierDTO)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        return new ResponseEntity<>(result.getData(), HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(result.getErrorMessage(), HttpStatus.NOT_FOUND);
                    }
                })
                .exceptionally(ex -> new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("admin/suppliers/delete/{id}")
    public CompletableFuture<ResponseEntity<?>> deleteSupplier(@PathVariable Long id) {
        return supplierService.deleteSupplier(id)
                .thenApply(result -> {
                    if (result.isSuccess()) {
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    } else {
                        return new ResponseEntity<>(result.getErrorMessage(), HttpStatus.NOT_FOUND);
                    }
                })
                .exceptionally(ex -> new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
