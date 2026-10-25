
package ktpm.uet.vnu.auction.infrastructure.config;

import ktpm.uet.vnu.auction.domain.port.AuctionRepository;
import ktpm.uet.vnu.auction.domain.service.AuctionService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AuctionBeanConfig {

    @Bean
    public AuctionService auctionService(
            AuctionRepository auctionRepository,
            Clock clock) {

        return new AuctionService(auctionRepository, clock);
    }
}
