package io.github.alexisTrejo11.drugstore.address.repository;

import io.github.alexisTrejo11.drugstore.address.entity.AddressEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {

    // Find addresses by user
    List<AddressEntity> findByUserIdAndActiveTrue(String userId);
    
    Page<AddressEntity> findByActiveTrue(Pageable pageable);
    
    Optional<AddressEntity> findByIdAndActiveTrue(UUID id);
    
    Optional<AddressEntity> findByIdAndUserIdAndActiveTrue(UUID id, String userId);
    
    // Count user addresses
    long countByUserIdAndActiveTrue(String userId);
    
    // Check if user has any address
    boolean existsByUserIdAndActiveTrue(String userId);
    
    // Find default address
    Optional<AddressEntity> findByUserIdAndIsDefaultTrueAndActiveTrue(String userId);
    
    // Admin queries
    Page<AddressEntity> findByUserIdAndActiveTrue(String userId, Pageable pageable);
    
    List<AddressEntity> findByCountryAndActiveTrue(String country);
    
    // Update operations
    @Modifying
    @Query("UPDATE AddressEntity a SET a.isDefault = false WHERE a.userId = :userId AND a.isDefault = true")
    void resetDefaultAddressForUser(@Param("userId") String userId);
    
    @Modifying
    @Query("UPDATE AddressEntity a SET a.active = false WHERE a.id = :id")
    void softDeleteById(@Param("id") String id);
    
    @Modifying
    @Query("UPDATE AddressEntity a SET a.active = false WHERE a.userId = :userId")
    void softDeleteByUserId(@Param("userId") String userId);
}