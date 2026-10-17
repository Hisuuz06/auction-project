package ktpm.uet.vnu.auction.domain.port;

/** Băm và so khớp mật khẩu. Bản cài đặt (BCrypt) nằm ở infrastructure/security. */
public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
