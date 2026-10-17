package ktpm.uet.vnu.auction.api.error;

import ktpm.uet.vnu.auction.domain.exception.ErrorCode;

/** Ánh xạ mã lỗi nghiệp vụ sang HTTP status (khớp bảng mã lỗi trong API spec). */
public final class ErrorStatus {

    private ErrorStatus() {
    }

    public static int of(ErrorCode code) {
        return switch (code) {
            case VALIDATION_ERROR -> 400;
            case UNAUTHORIZED, INVALID_CREDENTIALS -> 401;
            case FORBIDDEN, SELLER_CANNOT_BID -> 403;
            case AUCTION_NOT_FOUND -> 404;
            case USER_ALREADY_EXISTS, AUCTION_HAS_BIDS, AUCTION_ENDED, BID_TOO_LOW -> 409;
        };
    }
}
