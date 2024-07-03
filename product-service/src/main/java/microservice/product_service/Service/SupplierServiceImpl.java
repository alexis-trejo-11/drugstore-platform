package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierReturnDTO;
import microservice.product_service.Mappers.SupplierMapper;
import microservice.product_service.Model.Supplier;
import microservice.product_service.Repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Async
    @Transactional
    public void insertSupplier(SupplierInsertDTO supplierInsertDTO) {
        try {
            Supplier supplier = supplierMapper.insertDtoToSupplier(supplierInsertDTO);

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

            return optionalSupplier.map(supplierMapper::supplierToReturnDTO).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public SupplierReturnDTO getSupplierByName(String supplierMame) {
        try {
            Optional<Supplier> optionalSupplier = supplierRepository.findByName(supplierMame);

            return optionalSupplier.map(supplierMapper::supplierToReturnDTO).orElse(null);
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
                    .map(supplierMapper::supplierToReturnDTO)
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

