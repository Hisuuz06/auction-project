package ktpm.uet.vnu.auction.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** Clock dùng chung. Test có thể thay bằng Clock.fixed(...). */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
