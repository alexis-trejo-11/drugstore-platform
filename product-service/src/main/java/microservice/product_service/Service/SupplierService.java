package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Supplier.SupplierDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Supplier.SupplierInsertDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierService {
    void insertSupplier(SupplierInsertDTO supplierInsertDTO);
    SupplierDTO getSupplierById(Long supplierId);
    SupplierDTO getSupplierByName(String supplierMame);
    Page<SupplierDTO> getAllSuppliersSortedByName(Boolean sortedAsc, Pageable pageable);
    void updateSupplier(SupplierDTO supplierDTO);
    void deleteSupplier(Long supplierId);
}
