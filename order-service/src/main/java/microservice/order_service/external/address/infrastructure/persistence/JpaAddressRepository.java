package microservice.order_service.external.address.infrastructure.persistence;

import microservice.order_service.external.address.infrastructure.persistence.Model.DeliveryAddressModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaAddressRepository extends JpaRepository<DeliveryAddressModel, String> {

    @Query("SELECT a FROM DeliveryAddressModel a WHERE a.user.id = :userID AND a.isDefault = true")
    Optional<DeliveryAddressModel> findDefaultByUserId(@Param("userID") String userID);

    @Query("SELECT a FROM DeliveryAddressModel a WHERE a.user.id = :userID")
    List<DeliveryAddressModel> findByUserId(@Param("userID") String userID);

    int countByUserId(String userID);

}
