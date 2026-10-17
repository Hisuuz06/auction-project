package ktpm.uet.vnu.auction.domain.model;

import java.time.Instant;

/** Một lượt đặt giá. Java thuần. */
public record Bid(Long id, long auctionId, long bidderId, long amount, Instant createdAt) {
}
