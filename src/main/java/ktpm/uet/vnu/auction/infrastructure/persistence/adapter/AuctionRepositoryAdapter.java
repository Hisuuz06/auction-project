
package ktpm.uet.vnu.auction.infrastructure.persistence.adapter;

import ktpm.uet.vnu.auction.domain.model.Auction;
import ktpm.uet.vnu.auction.domain.port.AuctionRepository;
import ktpm.uet.vnu.auction.infrastructure.persistence.entity.AuctionEntity;
import ktpm.uet.vnu.auction.infrastructure.persistence.repository.AuctionJpaRepository;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class AuctionRepositoryAdapter implements AuctionRepository {

    private final AuctionJpaRepository jpaRepository;

    public AuctionRepositoryAdapter(AuctionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Auction save(Auction auction) {
        AuctionEntity entity = toEntity(auction);
        AuctionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Auction> findById(long id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Auction> findAllNewestFirst() {
        return jpaRepository.findAllByOrderByIdDesc()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Auction> findEndedWonBy(long userId, Instant now) {
        return jpaRepository.findEndedWonBy(userId, now)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(long id) {
        jpaRepository.deleteById(id);
    }

    // Chuyển Domain Model thành JPA Entity
    private AuctionEntity toEntity(Auction auction) {
        AuctionEntity entity = new AuctionEntity();

        entity.setId(auction.getId());
        entity.setSellerId(auction.getSellerId());
        entity.setName(auction.getName());
        entity.setDescription(auction.getDescription());
        entity.setStartingPrice(auction.getStartingPrice());
        entity.setMinIncrement(auction.getMinIncrement());
        entity.setCurrentPrice(auction.getCurrentPrice());
        entity.setHighestBidderId(auction.getHighestBidderId());
        entity.setEndTime(auction.getEndTime());

        return entity;
    }

    // Chuyển JPA Entity thành Domain Model
    private Auction toDomain(AuctionEntity entity) {
        return Auction.restore(
                entity.getId(),
                entity.getSellerId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStartingPrice(),
                entity.getMinIncrement(),
                entity.getCurrentPrice(),
                entity.getHighestBidderId(),
                entity.getEndTime()
        );
    }
}
