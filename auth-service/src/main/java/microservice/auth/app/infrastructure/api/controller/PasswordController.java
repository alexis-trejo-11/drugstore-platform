package microservice.auth.app.infrastructure.api.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/auth/password")
@RequiredArgsConstructor
public class PasswordController {

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseEntity<Void>> forgotPassword() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseEntity<Void>> resetPassword() {
        return ResponseEntity.ok().build();
    }
}
