package ktpm.uet.vnu.auction.api.error;

import java.time.Instant;

/** Định dạng lỗi chung của mọi endpoint (khớp mục 1 của API spec). */
public record ErrorResponse(Instant timestamp, int status, String code, String message, String path) {
}
