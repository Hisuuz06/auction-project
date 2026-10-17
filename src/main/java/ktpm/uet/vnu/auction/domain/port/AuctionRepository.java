package ktpm.uet.vnu.auction.domain.port;

import ktpm.uet.vnu.auction.domain.model.Auction;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Cổng truy cập dữ liệu phiên đấu giá. */
public interface AuctionRepository {

    /** Lưu mới (id null) hoặc cập nhật. Trả về phiên đã lưu, có id. */
    Auction save(Auction auction);

    Optional<Auction> findById(long id);

    /** Mới nhất trước (id giảm dần). */
    List<Auction> findAllNewestFirst();

    /** Phiên đã kết thúc (endTime < now) có highestBidderId = userId, endTime giảm dần. */
    List<Auction> findEndedWonBy(long userId, Instant now);

    void deleteById(long id);
}
