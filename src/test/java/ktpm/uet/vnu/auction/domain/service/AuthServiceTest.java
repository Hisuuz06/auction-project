package ktpm.uet.vnu.auction.domain.service;

import ktpm.uet.vnu.auction.domain.exception.DomainException;
import ktpm.uet.vnu.auction.domain.exception.ErrorCode;
import ktpm.uet.vnu.auction.domain.model.User;
import ktpm.uet.vnu.auction.domain.port.PasswordHasher;
import ktpm.uet.vnu.auction.domain.port.TokenService;
import ktpm.uet.vnu.auction.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Test cho AuthService của TV1 (Ngày 7).
 * Java thuần: chạy trực tiếp trong mili-giây, không cần bật Spring hay DB.
 */
class AuthServiceTest {

    private AuthService authService;
    private InMemoryUserRepository userRepository;
    private SimplePasswordHasher passwordHasher;
    private SimpleTokenService tokenService;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        passwordHasher = new SimplePasswordHasher();
        tokenService = new SimpleTokenService();
        authService = new AuthService(userRepository, passwordHasher, tokenService);
    }

    @Test
    void registerSuccess() {
        User user = authService.register("an.nguyen", "Passw0rd!");

        assertNotNull(user);
        assertEquals(1L, user.id());
        assertEquals("an.nguyen", user.username());
        assertEquals("hashed_Passw0rd!", user.passwordHash());
    }

    @Test
    void registerThrowsExceptionWhenUsernameAlreadyExists() {
        authService.register("an.nguyen", "Passw0rd!");

        DomainException exception = assertThrows(DomainException.class, () ->
                authService.register("an.nguyen", "AnotherPass123!")
        );

        assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getCode());
    }

    @Test
    void loginSuccess() {
        authService.register("an.nguyen", "Passw0rd!");

        AuthService.LoginResult result = authService.login("an.nguyen", "Passw0rd!");

        assertNotNull(result);
        assertEquals("jwt_token_for_1", result.token());
        assertEquals(3600L, result.expiresInSeconds());
    }

    @Test
    void loginFailsWhenUserNotFound() {
        DomainException exception = assertThrows(DomainException.class, () ->
                authService.login("unknown_user", "Passw0rd!")
        );

        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getCode());
    }

    @Test
    void loginFailsWhenPasswordIncorrect() {
        authService.register("an.nguyen", "Passw0rd!");

        DomainException exception = assertThrows(DomainException.class, () ->
                authService.login("an.nguyen", "WrongPassword!")
        );

        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getCode());
    }

    // --- Giả lập (Stub) các Port để test độc lập ---

    static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> usersByUsername = new HashMap<>();
        private long currentId = 1L;

        @Override
        public User save(User user) {
            User saved = new User(currentId++, user.username(), user.passwordHash());
            usersByUsername.put(saved.username(), saved);
            return saved;
        }

        @Override
        public Optional<User> findByUsername(String username) {
            return Optional.ofNullable(usersByUsername.get(username));
        }

        @Override
        public boolean existsByUsername(String username) {
            return usersByUsername.containsKey(username);
        }
    }

    static class SimplePasswordHasher implements PasswordHasher {
        @Override
        public String hash(String rawPassword) {
            return "hashed_" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String passwordHash) {
            return ("hashed_" + rawPassword).equals(passwordHash);
        }
    }

    static class SimpleTokenService implements TokenService {
        @Override
        public String generate(User user) {
            return "jwt_token_for_" + user.id();
        }

        @Override
        public Optional<Long> verify(String token) {
            return Optional.of(1L);
        }

        @Override
        public long expiresInSeconds() {
            return 3600L;
        }
    }
}
