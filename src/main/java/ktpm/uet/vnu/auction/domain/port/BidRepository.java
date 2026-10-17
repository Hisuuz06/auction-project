package ktpm.uet.vnu.auction.domain.port;

import ktpm.uet.vnu.auction.domain.model.Bid;

import java.util.List;

/** Cổng truy cập dữ liệu lượt đặt giá. */
public interface BidRepository {

    Bid save(Bid bid);

    /** Lịch sử bid của một phiên, amount giảm dần. */
    List<Bid> findByAuctionIdOrderByAmountDesc(long auctionId);
}
