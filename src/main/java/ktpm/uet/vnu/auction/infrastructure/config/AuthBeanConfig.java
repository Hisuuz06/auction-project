package ktpm.uet.vnu.auction.infrastructure.config;

import ktpm.uet.vnu.auction.domain.port.PasswordHasher;
import ktpm.uet.vnu.auction.domain.port.TokenService;
import ktpm.uet.vnu.auction.domain.port.UserRepository;
import ktpm.uet.vnu.auction.domain.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthBeanConfig {

    @Bean
    public AuthService authService(UserRepository users, PasswordHasher hasher, TokenService tokens) {
        return new AuthService(users, hasher, tokens);
    }
}
