package ktpm.uet.vnu.auction.infrastructure.persistence.adapter;

import ktpm.uet.vnu.auction.domain.model.User;
import ktpm.uet.vnu.auction.domain.port.UserRepository;
import ktpm.uet.vnu.auction.infrastructure.persistence.entity.UserEntity;
import ktpm.uet.vnu.auction.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository repository;

    public UserRepositoryAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    private User toDomain(UserEntity entity) {
        return new User(entity.getId(), entity.getUsername(), entity.getPassword());
    }

    private UserEntity toEntity(User user) {
        return new UserEntity(user.id(), user.username(), user.passwordHash());
    }
}
