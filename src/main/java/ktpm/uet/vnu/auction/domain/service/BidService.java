package ktpm.uet.vnu.auction.domain.service;

import ktpm.uet.vnu.auction.domain.exception.DomainException;
import ktpm.uet.vnu.auction.domain.exception.ErrorCode;
import ktpm.uet.vnu.auction.domain.model.Auction;
import ktpm.uet.vnu.auction.domain.model.Bid;
import ktpm.uet.vnu.auction.domain.port.AuctionRepository;
import ktpm.uet.vnu.auction.domain.port.BidRepository;
import ktpm.uet.vnu.auction.domain.port.TransactionRunner;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/** Đặt giá và lịch sử bid. Chủ sở hữu: TV3. Java thuần. */
public class BidService {

    private final AuctionRepository auctions;
    private final BidRepository bids;
    private final TransactionRunner tx;
    private final Clock clock;

    public BidService(AuctionRepository auctions, BidRepository bids,
                      TransactionRunner tx, Clock clock) {
        this.auctions = auctions;
        this.bids = bids;
        this.tx = tx;
        this.clock = clock;
    }

    /** GET /api/auctions/{id}/bids (hàm mẫu đã làm sẵn). */
    public List<Bid> listByAuction(long auctionId) {
        auctions.findById(auctionId)
                .orElseThrow(() -> new DomainException(
                        ErrorCode.AUCTION_NOT_FOUND, "Không tìm thấy phiên đấu giá " + auctionId));
        return bids.findByAuctionIdOrderByAmountDesc(auctionId);
    }

    /**
     * POST /api/auctions/{id}/bids. Thứ tự kiểm tra:
     * 1. Không có phiên: AUCTION_NOT_FOUND
     * 2. Phiên đã kết thúc (BR-4): AUCTION_ENDED
     * 3. Người đặt là người bán (BR-5): SELLER_CANNOT_BID
     * 4. amount < auction.minNextBid() (BR-3): BID_TOO_LOW
     * 5. Hợp lệ (BR-7): auction.applyBid(...), lưu auction và bid trong cùng một tx.execute(...)
     *
     * Pha 1 KHÔNG xử lý đồng thời (không khóa): đây là baseline để pha 2 đo và cải tiến.
     */
    public Bid placeBid(long bidderId, long auctionId, long amount) {
        return tx.execute(() -> {
            Auction auction = auctions.findById(auctionId)
                    .orElseThrow(() -> new DomainException(
                            ErrorCode.AUCTION_NOT_FOUND, "Không tìm thấy phiên đấu giá " + auctionId));
            Instant now = clock.instant();
            if (auction.isEnded(now)) {
                throw new DomainException(ErrorCode.AUCTION_ENDED, "Phiên đấu giá đã kết thúc");
            }
            if (auction.getSellerId().equals(bidderId)) {
                throw new DomainException(ErrorCode.SELLER_CANNOT_BID, "Người bán không được đặt giá phiên của mình");
            }
            if (amount < auction.minNextBid()) {
                throw new DomainException(ErrorCode.BID_TOO_LOW,
                        "Giá đặt phải từ " + auction.minNextBid() + " trở lên");
            }
            auction.applyBid(bidderId, amount);
            auctions.save(auction);
            return bids.save(new Bid(null, auctionId, bidderId, amount, now));
        });
    }
}
