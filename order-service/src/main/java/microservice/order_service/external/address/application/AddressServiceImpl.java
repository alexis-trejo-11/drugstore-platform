package microservice.order_service.external.address.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.order_service.external.address.domain.exception.*;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.domain.ports.input.AddressService;
import microservice.order_service.external.address.domain.ports.output.AddressRepository;
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
@Slf4j
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final JpaUserRepository userRepository;
    private final OrderRepository orderRepository;

    @Override
    public DeliveryAddress getDefaultAddressByUserID(UserID userID) {
        log.info("Fetching default address for userId={}", userID.value());
        DeliveryAddress address = addressRepository.findDefaultByUserID(userID)
                .orElseThrow(() -> {
                    log.warn("Default address not found for userId={}", userID.value());
                    return new AddressNotFoundByUserIDErr(userID);
                });
        log.debug("Default address found id={} for userId={}", address.getId().value(), userID.value());
        return address;
    }

    @Override
    public DeliveryAddress getAddressByOrderID(OrderID orderID) {
        log.info("Fetching address for orderId={}", orderID.value());
        Order order = orderRepository.findByID(orderID)
                .orElseThrow(() -> {
                    log.warn("Order not found for orderId={}", orderID.value());
                    return new OrderNotFoundIDException(orderID);
                });

        if (order.getAddressID() == null) {
            log.warn("Order {} has no associated address", orderID.value());
            throw new OrderWithNoAddressConflict(orderID);
        }

        DeliveryAddress address = addressRepository.findByID(order.getAddressID())
                .orElseThrow(() -> {
                    log.warn("Address for orderId={} not found (addressId={})", orderID.value(), order.getAddressID().value());
                    return new AddressNotFoundByOrderIDErr(orderID);
                });
        log.debug("Found address id={} for orderId={}", address.getId().value(), orderID.value());
        return address;
    }

    @Override
    public DeliveryAddress getAddressByID(AddressID addressID) {
        log.info("Fetching address by id={}", addressID.value());
        return getAddressOrThrow(addressID);
    }

    @Override
    public List<DeliveryAddress> getAddressesByUserID(UserID userID) {
        log.info("Fetching all addresses for userId={}", userID.value());
        List<DeliveryAddress> addresses = addressRepository.findAllByUserID(userID);
        log.debug("Found {} addresses for userId={}", addresses.size(), userID.value());
        return addresses;
    }

    @Override
    public AddressID createAddress(DeliveryAddress address) {
        log.info("Creating new address for userId={}", address.getUserID().value());

        if (!userRepository.existsById(address.getUserID().value())) {
            log.warn("Cannot create address: user not found userId={}", address.getUserID().value());
            throw new UserNotFoundByIDErr(address.getUserID());
        }

        List<DeliveryAddress> userAddresses = addressRepository.findAllByUserID(address.getUserID());
        if (userAddresses.isEmpty()) {
            log.info("No existing addresses found for userId={}, marking as default", address.getUserID().value());
            address.markAsDefault();
        } else if (address.isDefault()) {
            log.info("New address requests default for userId={}, unsetting previous defaults", address.getUserID().value());
            for (DeliveryAddress addr : userAddresses) {
                addr.unsetDefault();
            }
            try {
                addressRepository.bulkUpdate(userAddresses);
                log.debug("Unset default on {} previous addresses for userId={}", userAddresses.size(), address.getUserID().value());
            } catch (Exception ex) {
                log.error("Failed to bulk-update previous addresses for userId={}: {}", address.getUserID().value(), ex.getMessage(), ex);
                throw ex;
            }
        }

        DeliveryAddress addressCreated;
        try {
            addressCreated = addressRepository.save(address);
            log.info("Address created: id={} for userId={}", addressCreated.getId().value(), addressCreated.getUserID().value());
        } catch (Exception ex) {
            log.error("Failed to persist new address for userId={}: {}", address.getUserID().value(), ex.getMessage(), ex);
            throw ex;
        }
        return addressCreated.getId();
    }

    @Override
    public void updateAddress(DeliveryAddress address) {
        log.info("Updating address id={}", address.getId().value());
        DeliveryAddress existingAddress = getAddressOrThrow(address.getId());
        if (orderRepository.existsAnyByAddressIDAndOngoingStatus(address.getId())) {
            log.warn("Cannot update address id={} because it's referenced by ongoing orders", address.getId().value());
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
        try {
            addressRepository.save(addressUpdated);
            log.info("Address updated: id={}", addressUpdated.getId().value());
        } catch (Exception ex) {
            log.error("Failed to persist address update id={}: {}", addressUpdated.getId().value(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void setDefaultAddress(UserID userID, AddressID addressID) {
        log.info("Setting default address id={} for userId={}", addressID.value(), userID.value());
        List<DeliveryAddress> userAddresses = addressRepository.findAllByUserID(userID);

        boolean found = false;
        for (DeliveryAddress address : userAddresses) {
            if (address.getId().value().equals(addressID.value())) {
                address.markAsDefault();
                found = true;
                log.debug("Marked address id={} as default for userId={}", addressID.value(), userID.value());
            } else {
                address.unsetDefault();
            }
        }
        if (!found) {
            log.warn("Default address id={} not found for userId={}", addressID.value(), userID.value());
            throw new AddressNotFoundForUserErr(userID, addressID);
        }

        try {
            addressRepository.bulkUpdate(userAddresses);
            log.info("Default address set id={} for userId={}", addressID.value(), userID.value());
        } catch (Exception ex) {
            log.error("Failed to set default address id={} for userId={}: {}", addressID.value(), userID.value(), ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteAddress(AddressID addressID) {
        log.info("Deleting address id={}", addressID.value());
        DeliveryAddress address = getAddressOrThrow(addressID);

        if (orderRepository.existsAnyByAddressIDAndOngoingStatus(addressID)) {
            log.warn("Cannot delete address id={} because it's referenced by ongoing orders", addressID.value());
            throw new AddressInUseConflict(addressID);
        }


        if (address.isDefault()) {
            log.info("Address id={} is default; attempting to promote another default for userId={}", addressID.value(), address.getUserID().value());
            List<DeliveryAddress> userAddresses = addressRepository.findAllByUserID(address.getUserID());
            if (userAddresses.size() > 1) {
                for (DeliveryAddress addr : userAddresses) {
                    if (!addr.getId().value().equals(addressID.value())) {
                        addr.markAsDefault();
                        log.debug("Promoted address id={} to default for userId={}", addr.getId().value(), address.getUserID().value());
                        break;
                    }
                }
                try {
                    addressRepository.bulkUpdate(userAddresses);
                    log.info("Promoted another address to default for userId={}", address.getUserID().value());
                } catch (Exception ex) {
                    log.error("Failed to promote another address to default for userId={}: {}", address.getUserID().value(), ex.getMessage(), ex);
                    throw ex;
                }
            }
        }

        try {
            addressRepository.delete(address);
            log.info("Address deleted id={}", addressID.value());
        } catch (Exception ex) {
            log.error("Failed to delete address id={}: {}", addressID.value(), ex.getMessage(), ex);
            throw ex;
        }
    }

    private DeliveryAddress getAddressOrThrow(AddressID addressID) {
        return addressRepository.findByID(addressID)
                .orElseThrow(() -> {
                    log.warn("Address not found id={}", addressID.value());
                    return new AddressNotFoundByIDErr(addressID);
                });
    }


}
