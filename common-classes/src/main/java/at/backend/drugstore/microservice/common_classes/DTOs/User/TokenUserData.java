package at.backend.drugstore.microservice.common_classes.DTOs.User;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TokenUserData {

    private Long userId;
    private Long clientId;
    private List<String> roles;
}
