package microservice.order_service.external.users.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private UserID id;
    private String name;
    private String phoneNumber;
    private String email;
    private String status;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DeliveryAddress> addresses;

    // TODO: Check Equals Object Value properly works
    public Optional<DeliveryAddress> findAddressByID(AddressID addressID) {
        if (addresses == null || addresses.isEmpty()) {
            return Optional.empty();
        }
        return addresses.stream().filter(address -> address.getId().equals(addressID)).findFirst();
    }

    public static User create(String name, String email, String phoneNumber, String role, String status) {
        return new User(
                UserID.generate(),
                name,
                email,
                phoneNumber,
                role,
                status,
                LocalDateTime.now(),
                LocalDateTime.now(),
                new ArrayList<>());
    }
}
