package ktpm.uet.vnu.auction.infrastructure.persistence.repository;

import ktpm.uet.vnu.auction.infrastructure.persistence.entity.BidEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidJpaRepository extends JpaRepository<BidEntity, Long> {
    List<BidEntity> findByAuctionIdOrderByAmountDesc(Long auctionId);
}
