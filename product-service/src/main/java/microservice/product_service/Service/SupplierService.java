package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierReturnDTO;
import microservice.product_service.Model.Supplier;
import microservice.product_service.Repository.SupplierRepository;
import microservice.product_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Async
    @Transactional
    public void insertSupplier(SupplierInsertDTO supplierInsertDTO) {
        try {
            Supplier supplier = ModelTransformer.insertDtoToSupplier(supplierInsertDTO);

            supplierRepository.saveAndFlush(supplier);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while inserting the supplier", e);
        }
    }

    @Async
    @Transactional
    public SupplierReturnDTO getSupplierById(Long supplierId) {
        try {
            Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierId);

            return optionalSupplier.map(ModelTransformer::supplierToReturnDTO).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public SupplierReturnDTO getSupplierByName(String name) {
        try {
            Optional<Supplier> optionalSupplier = supplierRepository.findByName(name);

            return optionalSupplier.map(ModelTransformer::supplierToReturnDTO).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public List<SupplierReturnDTO> getAllSuppliers() {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();

            return suppliers.stream()
                    .map(ModelTransformer::supplierToReturnDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public boolean updateSupplier(SupplierReturnDTO supplierDTO) {
        try {
            Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierDTO.getId());
            if (optionalSupplier.isEmpty()) {
                return false;
            }
            Supplier supplier = optionalSupplier.get();
            supplier.setName(supplierDTO.getName());
            supplier.setContactInfo(supplierDTO.getContactInfo());
            supplier.setAddress(supplierDTO.getAddress());
            supplier.setPhone(supplierDTO.getPhone());
            supplier.setEmail(supplierDTO.getEmail());

            supplierRepository.save(supplier);

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public boolean deleteSupplier(Long id) {
        try {
            Optional<Supplier> optionalSupplier = supplierRepository.findById(id);
            if (optionalSupplier.isEmpty()) {
                return false;
            }
            Supplier supplier = optionalSupplier.get();

            supplierRepository.delete(supplier);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

