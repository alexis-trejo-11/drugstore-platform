package microservice.auth.app.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class LogoutController {
    @PostMapping("/logout")
    public ResponseEntity<ResponseEntity<Void>> logout() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ResponseEntity<Void>> logoutAll() {
        return ResponseEntity.ok().build();
    }

}
