package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Supplier.SupplierReturnDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.product_service.Model.Supplier;
import microservice.product_service.Repository.SupplierRepository;
import microservice.product_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public CompletableFuture<Result<SupplierReturnDTO>> insertSupplier(SupplierInsertDTO supplierInsertDTO) {
        try {
            // Create a new Supplier object from the SupplierDTO
            Supplier supplier = ModelTransformer.insertDtoToSupplier(supplierInsertDTO);

            // Save the new supplier to the database
            supplierRepository.saveAndFlush(supplier);

            // Create and return a SupplierDTO for the newly inserted supplier
            SupplierReturnDTO supplierReturnDTO = ModelTransformer.supplierToReturnDTO(supplier);
            return CompletableFuture.completedFuture(Result.success(supplierReturnDTO));
        } catch (Exception e) {
            // Handle any exceptions that occur during the insertion process
            e.printStackTrace(); // For logging purposes
            throw new RuntimeException("An error occurred while inserting the supplier", e);
        }
    }

    @Transactional
    public CompletableFuture<Result<SupplierReturnDTO>> getSupplierById(Long id) {
        try {
            // Attempt to find the supplier by its ID
            Optional<Supplier> supplier = supplierRepository.findById(id);
            if (supplier.isPresent()) {
                // Create and return a SupplierDTO for the found supplier
                SupplierReturnDTO supplierReturnDTO = ModelTransformer.supplierToReturnDTO(supplier.get());
                return CompletableFuture.completedFuture(Result.success(supplierReturnDTO));
            } else {
                throw new EntityNotFoundException("Supplier with ID: " + id + " not found.");
            }
        } catch (EntityNotFoundException e) {
            return CompletableFuture.failedFuture(e);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("An error occurred while fetching the supplier by ID", e));
        }
    }

    @Transactional
    public CompletableFuture<Result<SupplierReturnDTO>> getSupplierByName(String name) {
        try {
            // Attempt to find the supplier by its name
            Optional<Supplier> supplier = supplierRepository.findByName(name);
            if (supplier.isPresent()) {
                // Create and return a SupplierDTO for the found supplier
                SupplierReturnDTO supplierDTO = ModelTransformer.supplierToReturnDTO(supplier.get());
                return CompletableFuture.completedFuture(Result.success(supplierDTO));
            } else {
                throw new EntityNotFoundException("Supplier with name: " + name + " not found.");
            }
        } catch (EntityNotFoundException e) {
            return CompletableFuture.failedFuture(e);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("An error occurred while fetching the supplier by name", e));
        }
    }


    @Transactional
    public CompletableFuture<List<SupplierReturnDTO>> getAllSuppliers() {
        try {
            // Retrieve all suppliers from the database
            List<Supplier> suppliers = supplierRepository.findAll();

            // Transform the list of suppliers into a list of SupplierDTOs
            List<SupplierReturnDTO> supplierDTOs = suppliers.stream()
                    .map(ModelTransformer::supplierToReturnDTO)
                    .collect(Collectors.toList());

            return CompletableFuture.completedFuture(supplierDTOs);
        } catch (Exception e) {
            // Handle any exception and return a completed future with an error
            return CompletableFuture.failedFuture(new RuntimeException("An error occurred while fetching all suppliers", e));
        }
    }

    @Transactional
    public CompletableFuture<Result<SupplierReturnDTO>> updateSupplier(Long id, SupplierReturnDTO supplierDTO) {
        try {
            // Find the supplier by ID
            Optional<Supplier> optionalSupplier = supplierRepository.findById(id);
            if (optionalSupplier.isPresent()) {
                // Update supplier properties using constructor
                Supplier supplier = optionalSupplier.get();
                supplier.setName(supplierDTO.getName());
                supplier.setContactInfo(supplierDTO.getContactInfo());
                supplier.setAddress(supplierDTO.getAddress());
                supplier.setPhone(supplierDTO.getPhone());
                supplier.setEmail(supplierDTO.getEmail());

                // Save the updated supplier
                Supplier updatedSupplier = supplierRepository.save(supplier);

                return CompletableFuture.completedFuture(Result.success(ModelTransformer.supplierToReturnDTO(updatedSupplier)));
            } else {
                throw new EntityNotFoundException("Supplier with ID: " + id + " not found.");
            }
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("An error occurred while updating the supplier", e));
        }
    }

    @Transactional
    public CompletableFuture<Result<SupplierReturnDTO>> deleteSupplier(Long id) {
        try {
            // Find the supplier by ID
            Optional<Supplier> optionalSupplier = supplierRepository.findById(id);
            if (optionalSupplier.isPresent()) {
                // Make DTO before removing the entity
                Supplier supplier = optionalSupplier.get();
                SupplierReturnDTO supplierDTO = ModelTransformer.supplierToReturnDTO(supplier);

                // Delete the supplier
                supplierRepository.delete(supplier);

                return CompletableFuture.completedFuture(Result.success(supplierDTO));
            } else {
                throw new EntityNotFoundException("Supplier with ID: " + id + " not found.");
            }
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("An error occurred while deleting the supplier", e));
        }
    }
}

