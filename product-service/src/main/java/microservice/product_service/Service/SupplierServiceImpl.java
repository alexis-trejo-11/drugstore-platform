package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Supplier.SupplierDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Supplier.SupplierInsertDTO;
import microservice.product_service.Mappers.SupplierMapper;
import microservice.product_service.Model.Supplier;
import microservice.product_service.Repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    @Override
    @Cacheable(value = "supplierById", key = "#supplierId")
    public SupplierDTO getSupplierById(Long supplierId) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierId);
        return optionalSupplier.map(supplierMapper::entityToDTO).orElse(null);
    }

    @Override
    @Cacheable(value = "supplierByName", key = "#supplierName")
    public SupplierDTO getSupplierByName(String supplierMame) {
            Optional<Supplier> optionalSupplier = supplierRepository.findByName(supplierMame);
            return optionalSupplier.map(supplierMapper::entityToDTO).orElse(null);
    }

    @Override
    @Cacheable(value = "allSuppliersSortedByName", key = "#sortedAsc + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<SupplierDTO> getAllSuppliersSortedByName(Boolean sortedAsc, Pageable pageable) {
        // Determine sort direction
        Sort sort = Sort.by("name");
        sort = sortedAsc ? sort.ascending() : sort.descending();

        // Apply sort to pageable
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // Fetch suppliers with dynamic sorting
        Page<Supplier> supplierPage = supplierRepository.findAll(sortedPageable);
        return supplierPage.map(supplierMapper::entityToDTO);
    }

    @Transactional
    public void insertSupplier(SupplierInsertDTO supplierInsertDTO) {
        Supplier supplier = supplierMapper.insertDtoToSupplier(supplierInsertDTO);
        supplierRepository.saveAndFlush(supplier);
    }

    @Transactional
    public void updateSupplier(SupplierDTO supplierDTO) {
            Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierDTO.getId());
            if (optionalSupplier.isEmpty()) {
                throw new RuntimeException();
            }

            Supplier supplier = optionalSupplier.get();
            supplier.setName(supplierDTO.getName());
            supplier.setContactInfo(supplierDTO.getContactInfo());
            supplier.setAddress(supplierDTO.getAddress());
            supplier.setPhone(supplierDTO.getPhone());
            supplier.setEmail(supplierDTO.getEmail());

            supplierRepository.saveAndFlush(supplier);
    }

    @Transactional
    public void deleteSupplier(Long supplierId) {
            Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierId);
            if (optionalSupplier.isEmpty()) {
                throw new RuntimeException();
            }
            Supplier supplier = optionalSupplier.get();

            supplierRepository.delete(supplier);
    }

    @Override
    public boolean validateExistingSupplier(Long supplierId) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(supplierId);
        return optionalSupplier.isPresent();
    }
}

