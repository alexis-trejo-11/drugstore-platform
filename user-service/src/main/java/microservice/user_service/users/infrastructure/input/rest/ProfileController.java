package microservice.user_service.users.infrastructure.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/users/profile")
public class ProfileController {

    @GetMapping
    public ResponseEntity<Object> getProfile() {
        return ResponseEntity.ok("User profile data");
    }

    @PatchMapping
    public ResponseEntity<Object> updateProfile() {
        return ResponseEntity.ok("User profile updated successfully");
    }
}
