package ktpm.uet.vnu.auction.api.dto;

import ktpm.uet.vnu.auction.domain.model.Bid;

import java.time.Instant;

public record BidResponse(Long id, long auctionId, long bidderId, long amount, Instant createdAt) {

    /** Mapper tầng API: đổi Bid (domain) thành dạng JSON trả cho client. */
    public static BidResponse from(Bid bid) {
        return new BidResponse(bid.id(), bid.auctionId(), bid.bidderId(), bid.amount(), bid.createdAt());
    }
}