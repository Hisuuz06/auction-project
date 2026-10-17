package ktpm.uet.vnu.auction.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Ví dụ test tầng nghiệp vụ: không cần Spring, không cần DB, chạy trong mili giây. */
class AuctionTest {

    private static final Instant END = Instant.parse("2026-11-01T10:00:00Z");

    private Auction newAuction() {
        return Auction.create(1L, "Đồng hồ cơ cổ 1970", null, 500_000, 50_000, END);
    }

    @Test
    void minNextBidIsStartingPriceWhenNoBids() {
        Auction auction = newAuction();
        assertFalse(auction.hasBids());
        assertEquals(500_000, auction.minNextBid());
    }

    @Test
    void minNextBidAddsIncrementAfterAValidBid() {
        Auction auction = newAuction();
        auction.applyBid(5L, 700_000);
        assertTrue(auction.hasBids());
        assertEquals(750_000, auction.minNextBid());
        assertEquals(5L, auction.getHighestBidderId());
    }

    @Test
    void statusIsDerivedFromEndTime() {
        Auction auction = newAuction();
        assertEquals(AuctionStatus.ACTIVE, auction.status(END.minusSeconds(1)));
        assertEquals(AuctionStatus.ENDED, auction.status(END.plusSeconds(1)));
    }
}
