package microservice.order_service.external.address.domain.ports.input;

import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.util.List;

public interface AddressService {
    DeliveryAddress getDefaultAddressByUserID(UserID userID);
    DeliveryAddress getAddressByOrderID(OrderID orderID);
    DeliveryAddress getAddressByID(AddressID addressID);
    List<DeliveryAddress> getAddressesByUserID(UserID userID);

    AddressID createAddress(DeliveryAddress address);
    void updateAddress(DeliveryAddress address);
    void setDefaultAddress(UserID userID, AddressID addressID);
    void deleteAddress(AddressID addressID);
}
