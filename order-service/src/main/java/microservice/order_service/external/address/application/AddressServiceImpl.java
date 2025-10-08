package microservice.order_service.external.address.application;

import lombok.RequiredArgsConstructor;
import microservice.order_service.external.address.domain.exception.*;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.domain.ports.input.AddressService;
import microservice.order_service.external.address.domain.ports.output.AddressRepository;
import microservice.order_service.external.users.application.service.UserService;
import microservice.order_service.external.users.domain.exceptions.UserNotFoundByIDErr;
import microservice.order_service.external.users.infrastructure.persistence.repository.JpaUserRepository;
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
    private final JpaUserRepository userRepository;
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
        if (!userRepository.existsById(address.getUserID().value())) {
            throw new UserNotFoundByIDErr(address.getUserID());
        }

        List<DeliveryAddress> userAddresses = addressRepository.findAllByUserID(address.getUserID());
        if (userAddresses.isEmpty()) {
            address.markAsDefault();
        } else if (address.isDefault()) {
            for (DeliveryAddress addr : userAddresses) {
                addr.unsetDefault();
            }
            addressRepository.bulkUpdate(userAddresses);
        }

        DeliveryAddress addressCreated = addressRepository.save(address);
        return addressCreated.getId();
    }

    @Override
    public void updateAddress(DeliveryAddress address) {
        DeliveryAddress existingAddress = getAddressOrThrow(address.getId());
        if (orderRepository.existsAnyByAddressIDAndOngoingStatus(address.getId())) {
            throw new AddressInUseConflict(address.getId());
        }

        var addressUpdated = existingAddress.update(
                address.getCountry(),
                address.getState(),
                address.getCity(),
                address.getStreet(),
                address.getInnerNumber(),
                address.getOuterNumber(),
                address.getNeighborhood(),
                address.getBuildingType(),
                address.getZipCode(),
                address.getAdditionalInfo()
        );
        addressRepository.save(addressUpdated);
    }

    @Override
    public void setDefaultAddress(UserID userID, AddressID addressID) {
        List<DeliveryAddress> userAddresses = addressRepository.findAllByUserID(userID);

        boolean found = false;
        for (DeliveryAddress address : userAddresses) {
            if (address.getId().value().equals(addressID.value())) {
                address.markAsDefault();
                found = true;
            } else {
                address.unsetDefault();
            }
        }
        if (!found) {
            throw new AddressNotFoundForUserErr(userID, addressID);
        }

        addressRepository.bulkUpdate(userAddresses);
    }

    @Override
    public void deleteAddress(AddressID addressID) {
        DeliveryAddress address = getAddressOrThrow(addressID);

        if (orderRepository.existsAnyByAddressIDAndOngoingStatus(addressID)) {
            throw new AddressInUseConflict(addressID);
        }


        if (address.isDefault()) {
            List<DeliveryAddress> userAddresses = addressRepository.findAllByUserID(address.getUserID());
            if (userAddresses.size() > 1) {
                for (DeliveryAddress addr : userAddresses) {
                    if (!addr.getId().value().equals(addressID.value())) {
                        addr.markAsDefault();
                        break;
                    }
                }
                addressRepository.bulkUpdate(userAddresses);
            }
        }

        addressRepository.delete(address);
    }

    private DeliveryAddress getAddressOrThrow(AddressID addressID) {
        return addressRepository.findByID(addressID)
                .orElseThrow(() -> new AddressNotFoundByIDErr(addressID));
    }


}
