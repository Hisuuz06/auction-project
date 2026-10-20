
package ktpm.uet.vnu.auction.infrastructure.persistence.repository;

import ktpm.uet.vnu.auction.infrastructure.persistence.entity.AuctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AuctionJpaRepository
        extends JpaRepository<AuctionEntity, Long> {

    // Lấy danh sách phiên đấu giá mới nhất trước
    List<AuctionEntity> findAllByOrderByIdDesc();

    // Lấy các phiên đã kết thúc mà user thắng
    @Query("""
        SELECT a FROM AuctionEntity a
        WHERE a.highestBidderId = :userId
          AND a.endTime < :now
        ORDER BY a.endTime DESC
        """)
    List<AuctionEntity> findEndedWonBy(
            @Param("userId") long userId,
            @Param("now") Instant now
    );
}
