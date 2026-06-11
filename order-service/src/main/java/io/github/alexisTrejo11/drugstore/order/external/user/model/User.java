package io.github.alexisTrejo11.drugstore.order.external.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

import java.time.LocalDateTime;

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

    public static User create(String name, String email, String phoneNumber, String role, String status) {
        return new User(
                UserID.generate(),
                name,
                email,
                phoneNumber,
                role,
                status,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}