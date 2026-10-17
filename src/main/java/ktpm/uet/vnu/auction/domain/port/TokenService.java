package ktpm.uet.vnu.auction.domain.port;

import ktpm.uet.vnu.auction.domain.model.User;

import java.util.Optional;

/** Phát hành và kiểm tra JWT. Bản cài đặt nằm ở infrastructure/security. */
public interface TokenService {

    String generate(User user);

    /** Trả về userId nếu token hợp lệ và còn hạn, ngược lại rỗng. */
    Optional<Long> verify(String token);

    long expiresInSeconds();
}
