package microservice.order_service.external.users.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
@Builder
public class User {
    private UserID id;
    private String phoneNumber;
    private String email;
    private String name;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private List<DeliveryAddress> addresses;

    // TODO: Check Equals Object Value properly works
    public Optional<DeliveryAddress> findAddressByID(AddressID addressID) {
        if (addresses == null || addresses.isEmpty()) {
            return Optional.empty();
        }
        return addresses.stream()
                .filter(address -> address.getId().equals(addressID))
                .findFirst();
    }
}
