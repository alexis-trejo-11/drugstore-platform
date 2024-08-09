package at.backend.drugstore.microservice.common_models.DTOs.User;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class TokenUserData {

    private Long userId;
    private Long clientId;
    private List<String> roles;
}
