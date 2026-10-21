package ktpm.uet.vnu.auction.infrastructure.config;

import ktpm.uet.vnu.auction.domain.port.AuctionRepository;
import ktpm.uet.vnu.auction.domain.port.BidRepository;
import ktpm.uet.vnu.auction.domain.port.TransactionRunner;
import ktpm.uet.vnu.auction.domain.service.BidService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BidBeanConfig {

    @Bean
    BidService bidService(AuctionRepository auctions, BidRepository bids,
                          TransactionRunner tx, Clock clock) {
        return new BidService(auctions, bids, tx, clock);
    }
}
