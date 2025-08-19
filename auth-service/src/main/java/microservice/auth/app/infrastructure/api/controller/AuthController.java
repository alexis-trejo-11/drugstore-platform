package microservice.auth.app.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<ResponseEntity<Void>> register() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseEntity<Void>> login() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-session")
    public ResponseEntity<ResponseEntity<Void>> refreshSession() {
        return ResponseEntity.ok().build();
    }
}
