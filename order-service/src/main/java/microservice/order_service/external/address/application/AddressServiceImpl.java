package microservice.order_service.external.address.application;

import lombok.RequiredArgsConstructor;
import microservice.order_service.external.address.domain.exception.*;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.domain.ports.input.AddressService;
import microservice.order_service.external.address.domain.ports.output.AddressRepository;
import microservice.order_service.orders.application.exceptions.OrderNotFoundIDException;
import microservice.order_service.orders.domain.models.Order;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import microservice.order_service.orders.domain.ports.output.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;

    @Override
    public DeliveryAddress getDefaultAddressByUserID(UserID userID) {
        return addressRepository.findDefaultByUserID(userID)
                .orElseThrow(() -> new AddressNotFoundByUserIDErr(userID));
    }

    @Override
    public DeliveryAddress getAddressByOrderID(OrderID orderID) {
        Order order = orderRepository.findByID(orderID)
                .orElseThrow(() -> new OrderNotFoundIDException(orderID));

        if (order.getAddressID() == null) {
            throw new OrderWithNoAddressConflict(orderID);
        }

        return addressRepository.findByID(order.getAddressID())
                .orElseThrow(() -> new AddressNotFoundByOrderIDErr(orderID));
    }

    @Override
    public DeliveryAddress getAddressByID(AddressID addressID) {
        return getAddressOrThrow(addressID);
    }

    @Override
    public List<DeliveryAddress> getAddressesByUserID(UserID userID) {
        return addressRepository.findAllByUserID(userID);
    }

    @Override
    public AddressID createAddress(DeliveryAddress address) {
        DeliveryAddress addressSaved = addressRepository.save(address);
        return addressSaved.getId();
    }

    @Override
    public void updateAddress(DeliveryAddress address) {
        getAddressOrThrow(address.getId());
        addressRepository.save(address);
    }

    @Override
    public void setDefaultAddress(UserID userID, AddressID addressID) {
        List<DeliveryAddress> userAddresses = addressRepository.findAllByUserID(userID);
        boolean found = false;
        for (DeliveryAddress address : userAddresses) {
            if (address.getId().equals(addressID)) {
                address.markAsDefault();
                found = true;
            } else {
                address.unsetDefault();
            }
        }

        if (!found) {
            throw new AddressNotFoundForUserErr(userID, addressID);
        }
    }

    @Override
    public void deleteAddress(AddressID addressID) {
        DeliveryAddress address = getAddressOrThrow(addressID);
        addressRepository.delete(address);
    }


    private DeliveryAddress getAddressOrThrow(AddressID addressID) {
        return addressRepository.findByID(addressID)
                .orElseThrow(() -> new AddressNotFoundByIDErr(addressID));
    }
}
