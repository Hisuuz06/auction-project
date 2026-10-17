package ktpm.uet.vnu.auction.domain.port;

import ktpm.uet.vnu.auction.domain.model.User;

import java.util.Optional;

/** Cổng truy cập dữ liệu người dùng. Bản cài đặt nằm ở infrastructure/persistence/adapter. */
public interface UserRepository {

    User save(User user);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
