package ktpm.uet.vnu.auction.domain.service;

import ktpm.uet.vnu.auction.domain.exception.DomainException;
import ktpm.uet.vnu.auction.domain.exception.ErrorCode;
import ktpm.uet.vnu.auction.domain.model.Auction;
import ktpm.uet.vnu.auction.domain.model.AuctionStatus;
import ktpm.uet.vnu.auction.domain.port.AuctionRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;


public class AuctionService {

    private final AuctionRepository auctions;
    private final Clock clock;

    public AuctionService(AuctionRepository auctions, Clock clock) {
        this.auctions = auctions;
        this.clock = clock;
    }

    /** GET /api/auctions */
    public List<Auction> list() {
        return auctions.findAllNewestFirst();
    }

    /** GET /api/auctions/{id}, không có thì ném AUCTION_NOT_FOUND. */
    public Auction get(long id) {
        return auctions.findById(id)
                .orElseThrow(() -> new DomainException(
                        ErrorCode.AUCTION_NOT_FOUND, "Không tìm thấy phiên đấu giá " + id));
    }

    /** Trạng thái hiện tại của một phiên (tầng API dùng để điền trường status). */
    public AuctionStatus statusOf(Auction auction) {
        return auction.status(clock.instant());
    }

    /** POST /api/auctions. BR-2: endTime phải ở tương lai (VALIDATION_ERROR). */
    public Auction create(long sellerId, String name, String description,
                          long startingPrice, long minIncrement, Instant endTime) {

        Instant now = clock.instant();

        // Kiểm tra thời gian kết thúc phải ở tương lai
        if (endTime == null || !endTime.isAfter(now)) {
            throw new DomainException(
                    ErrorCode.VALIDATION_ERROR,
                    "Thời gian kết thúc phải lớn hơn thời gian hiện tại"
            );
        }

        // Tạo đối tượng Auction
        Auction auction = Auction.create(
                sellerId,
                name,
                description,
                startingPrice,
                minIncrement,
                endTime
        );

        // Lưu phiên đấu giá vào database thông qua Repository
        return auctions.save(auction);
    }

    /**
     * DELETE /api/auctions/{id}. BR-6:
     * không có phiên: AUCTION_NOT_FOUND; không phải người bán: FORBIDDEN;
     * đã có bid (auction.hasBids()): AUCTION_HAS_BIDS.
     */
    public void delete(long requesterId, long auctionId) {

        // 1. Tìm phiên đấu giá
        Auction auction = get(auctionId);

        // 2. Kiểm tra quyền sở hữu
        if (!auction.getSellerId().equals(requesterId)) {
            throw new DomainException(
                    ErrorCode.FORBIDDEN,
                    "Bạn không có quyền xóa phiên đấu giá này"
            );
        }

        // 3. Kiểm tra phiên đã kết thúc chưa
        if (auction.isEnded(clock.instant())) {
            throw new DomainException(
                    ErrorCode.VALIDATION_ERROR,
                    "Không thể xóa phiên đấu giá đã kết thúc"
            );
        }

        // 4. Kiểm tra phiên đã có người đặt giá chưa
        if (auction.hasBids()) {
            throw new DomainException(
                    ErrorCode.AUCTION_HAS_BIDS,
                    "Không thể xóa phiên đấu giá đã có người đặt giá"
            );
        }

        // 5. Xóa phiên khỏi database
        auctions.deleteById(auctionId);
    }

    public List<Auction> listWonBy(long userId) {
        return auctions.findEndedWonBy(userId, clock.instant());
    }
}
