package ktpm.uet.vnu.auction.domain.service;

import ktpm.uet.vnu.auction.domain.model.User;
import ktpm.uet.vnu.auction.domain.port.PasswordHasher;
import ktpm.uet.vnu.auction.domain.port.TokenService;
import ktpm.uet.vnu.auction.domain.port.UserRepository;

/**
 * Đăng ký và đăng nhập. Chủ sở hữu: TV1.
 * Tầng này là Java thuần: không import Spring, JPA, Jackson, jjwt.
 */
public class AuthService {

    private final UserRepository users;
    private final PasswordHasher hasher;
    private final TokenService tokens;

    public AuthService(UserRepository users, PasswordHasher hasher, TokenService tokens) {
        this.users = users;
        this.hasher = hasher;
        this.tokens = tokens;
    }

    public record LoginResult(String token, long expiresInSeconds) {
    }

    /**
     * BR-1: username không trùng, nếu trùng ném DomainException(USER_ALREADY_EXISTS).
     * Băm mật khẩu bằng hasher rồi lưu.
     */
    public User register(String username, String rawPassword) {
        throw new UnsupportedOperationException("TODO TV1: register");
    }

    /**
     * Sai username hoặc mật khẩu: ném DomainException(INVALID_CREDENTIALS).
     * Đúng: trả token và thời hạn.
     */
    public LoginResult login(String username, String rawPassword) {
        throw new UnsupportedOperationException("TODO TV1: login");
    }
}
