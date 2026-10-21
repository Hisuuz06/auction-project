package ktpm.uet.vnu.auction.infrastructure.persistence.adapter;

import ktpm.uet.vnu.auction.domain.model.Bid;
import ktpm.uet.vnu.auction.domain.port.BidRepository;
import ktpm.uet.vnu.auction.infrastructure.persistence.entity.AuctionEntity;
import ktpm.uet.vnu.auction.infrastructure.persistence.entity.BidEntity;
import ktpm.uet.vnu.auction.infrastructure.persistence.entity.UserEntity;
import ktpm.uet.vnu.auction.infrastructure.persistence.repository.AuctionJpaRepository;
import ktpm.uet.vnu.auction.infrastructure.persistence.repository.BidJpaRepository;
import ktpm.uet.vnu.auction.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BidRepositoryAdapter implements BidRepository {

    private final BidJpaRepository bidJpa;
    private final AuctionJpaRepository auctionJpa;
    private final UserJpaRepository userJpa;

    public BidRepositoryAdapter(BidJpaRepository bidJpa,
                                AuctionJpaRepository auctionJpa,
                                UserJpaRepository userJpa) {
        this.bidJpa = bidJpa;
        this.auctionJpa = auctionJpa;
        this.userJpa = userJpa;
    }

    @Override
    public Bid save(Bid bid) {
        // getReferenceById chỉ tạo tham chiếu theo id, không truy vấn DB
        AuctionEntity auction = auctionJpa.getReferenceById(bid.auctionId());
        UserEntity bidder = userJpa.getReferenceById(bid.bidderId());
        BidEntity saved = bidJpa.save(new BidEntity(auction, bidder, bid.amount(), bid.createdAt()));
        return toDomain(saved);
    }

    @Override
    public List<Bid> findByAuctionIdOrderByAmountDesc(long auctionId) {
        return bidJpa.findByAuctionIdOrderByAmountDesc(auctionId).stream()
                .map(this::toDomain)
                .toList();
    }

    // Mapper: entity -> domain. Chỉ dùng id của auction/bidder nên không bị LazyInitializationException
    private Bid toDomain(BidEntity e) {
        return new Bid(e.getId(), e.getAuction().getId(), e.getBidder().getId(),
                e.getAmount(), e.getCreatedAt());
    }
}