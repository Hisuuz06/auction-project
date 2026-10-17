package ktpm.uet.vnu.auction.domain.exception;

/** Mã lỗi nghiệp vụ. Việc đổi sang HTTP status do tầng API làm (api/error/ErrorStatus). */
public enum ErrorCode {
    VALIDATION_ERROR,
    UNAUTHORIZED,
    INVALID_CREDENTIALS,
    FORBIDDEN,
    SELLER_CANNOT_BID,
    AUCTION_NOT_FOUND,
    USER_ALREADY_EXISTS,
    AUCTION_HAS_BIDS,
    AUCTION_ENDED,
    BID_TOO_LOW
}
