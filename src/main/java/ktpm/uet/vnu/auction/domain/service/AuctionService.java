package ktpm.uet.vnu.auction.domain.service;

import ktpm.uet.vnu.auction.domain.exception.DomainException;
import ktpm.uet.vnu.auction.domain.exception.ErrorCode;
import ktpm.uet.vnu.auction.domain.model.Auction;
import ktpm.uet.vnu.auction.domain.model.AuctionStatus;
import ktpm.uet.vnu.auction.domain.port.AuctionRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/** Quản lý phiên đấu giá. Chủ sở hữu: TV2. Java thuần. */
public class AuctionService {

    private final AuctionRepository auctions;
    private final Clock clock;

    public AuctionService(AuctionRepository auctions, Clock clock) {
        this.auctions = auctions;
        this.clock = clock;
    }

    // ---- Hai hàm mẫu đã làm sẵn, để cả nhóm theo cùng phong cách ----

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

    // ---- Phần việc của TV2 ----

    /** POST /api/auctions. BR-2: endTime phải ở tương lai (VALIDATION_ERROR). */
    public Auction create(long sellerId, String name, String description,
                          long startingPrice, long minIncrement, Instant endTime) {
        throw new UnsupportedOperationException("TODO TV2: create");
    }

    /**
     * DELETE /api/auctions/{id}. BR-6:
     * không có phiên: AUCTION_NOT_FOUND; không phải người bán: FORBIDDEN;
     * đã có bid (auction.hasBids()): AUCTION_HAS_BIDS.
     */
    public void delete(long requesterId, long auctionId) {
        throw new UnsupportedOperationException("TODO TV2: delete");
    }

    /** GET /api/users/me/won-auctions (BR-8). Dùng auctions.findEndedWonBy(userId, clock.instant()). */
    public List<Auction> listWonBy(long userId) {
        throw new UnsupportedOperationException("TODO TV2: listWonBy");
    }
}
