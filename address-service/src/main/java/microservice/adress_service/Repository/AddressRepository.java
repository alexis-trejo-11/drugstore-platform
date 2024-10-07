package microservice.adress_service.Repository;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressUpdateDTO;
import microservice.adress_service.Model.Address;
import microservice.adress_service.Model.ClientAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<ClientAddress, Long> {
     List<ClientAddress> findByClientId(long clientId);

     Optional<ClientAddress> findByIdAndClientId(long id, long clientId);

     @Modifying
     @Transactional
     @Query("UPDATE ClientAddress ca SET ca.street = :#{#addressDTO.street}, " +
             "ca.houseNumber = :#{#addressDTO.houseNumber}, " +
             "ca.neighborhood = :#{#addressDTO.neighborhood}, " +
             "ca.state = :#{#addressDTO.state}, " +
             "ca.country = :#{#addressDTO.country}, " +
             "ca.description = :#{#addressDTO.description}, " +
             "ca.zipCode = :#{#addressDTO.zipCode}, " +
             "ca.innerNumber = :#{#addressDTO.innerNumber}, " +
             "ca.addressType = :#{#addressDTO.addressType}, " +
             "ca.updatedAt = CURRENT_TIMESTAMP " +
             "WHERE ca.id = :addressId AND ca.clientId = :clientId")
     int updateClientAddress(@Param("addressId") Long addressId,
                             @Param("clientId") Long clientId,
                             @Param("addressDTO") AddressUpdateDTO addressDTO);
}


