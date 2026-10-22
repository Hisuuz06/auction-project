package ktpm.uet.vnu.auction.api.controller;

import jakarta.validation.Valid;
import ktpm.uet.vnu.auction.api.dto.LoginRequest;
import ktpm.uet.vnu.auction.api.dto.LoginResponse;
import ktpm.uet.vnu.auction.api.dto.RegisterRequest;
import ktpm.uet.vnu.auction.api.dto.UserResponse;
import ktpm.uet.vnu.auction.domain.model.User;
import ktpm.uet.vnu.auction.domain.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Endpoint #1: POST /api/auth/register (Công khai) */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponse(user.id(), user.username()));
    }

    /** Endpoint #2: POST /api/auth/login (Công khai) */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthService.LoginResult result = authService.login(request.username(), request.password());
        return ResponseEntity.ok(new LoginResponse(result.token(), "Bearer", result.expiresInSeconds()));
    }
}
