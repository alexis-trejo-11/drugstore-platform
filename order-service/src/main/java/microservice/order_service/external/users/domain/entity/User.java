package microservice.order_service.external.users.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

import java.time.LocalDateTime;
import java.util.List;

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
}
