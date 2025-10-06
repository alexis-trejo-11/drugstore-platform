package microservice.order_service.external.address.domain.ports.output;

import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.util.List;
import java.util.Optional;

public interface AddressRepository {
    Optional<DeliveryAddress> findByID(AddressID addressID);
    Optional<DeliveryAddress> findDefaultByUserID(UserID userID);
    List<DeliveryAddress> findAllByUserID(UserID userID);
    DeliveryAddress save(DeliveryAddress address);
    void bulkUpdate(List<DeliveryAddress> addresses);
    void delete(DeliveryAddress address);
}
