package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Supplier.SupplierInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Supplier.SupplierReturnDTO;

import java.util.List;

public interface SupplierService {
    void insertSupplier(SupplierInsertDTO supplierInsertDTO);
    SupplierReturnDTO getSupplierById(Long supplierId);
    SupplierReturnDTO getSupplierByName(String supplierMame);
    List<SupplierReturnDTO> getAllSuppliers();
    boolean updateSupplier(SupplierReturnDTO supplierDTO);
    boolean deleteSupplier(Long id);
}
