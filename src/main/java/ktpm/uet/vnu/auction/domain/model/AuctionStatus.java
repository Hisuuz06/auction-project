package ktpm.uet.vnu.auction.domain.model;

/** Trạng thái phiên. Không lưu trong DB, suy ra từ end_time khi đọc. */
public enum AuctionStatus {
    ACTIVE,
    ENDED
}
