package ktpm.uet.vnu.auction.domain.service;

import ktpm.uet.vnu.auction.domain.exception.DomainException;
import ktpm.uet.vnu.auction.domain.exception.ErrorCode;
import ktpm.uet.vnu.auction.domain.model.Auction;
import ktpm.uet.vnu.auction.domain.model.Bid;
import ktpm.uet.vnu.auction.domain.port.AuctionRepository;
import ktpm.uet.vnu.auction.domain.port.BidRepository;
import ktpm.uet.vnu.auction.domain.port.TransactionRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BidServiceTest {
    private static final Instant END = Instant.parse("2026-11-01T10:00:00Z");
    private final Clock clock = Clock.fixed(END.minusSeconds(3600), ZoneOffset.UTC);
    private final Map<Long, Auction> auctionStore = new HashMap<>();
    private final List<Bid> bidStore = new ArrayList<>();
    private BidService service;

    @BeforeEach
    void setUp() {
        AuctionRepository auctions = new AuctionRepository() {
            public Auction save(Auction a) { auctionStore.put(a.getId(), a); return a; }
            public Optional<Auction> findById(long id) { return Optional.ofNullable(auctionStore.get(id)); }
            public List<Auction> findAllNewestFirst() { return List.of(); }
            public List<Auction> findEndedWonBy(long u, Instant n) { return List.of(); }
            public void deleteById(long id) { auctionStore.remove(id); }
        };
        BidRepository bids = new BidRepository() {
            public Bid save(Bid b) { bidStore.add(b); return b; }
            public List<Bid> findByAuctionIdOrderByAmountDesc(long id) { return List.of(); }
        };
        TransactionRunner tx = new TransactionRunner() {
            public <T> T execute(Supplier<T> action) { return action.get(); }
        };
        service = new BidService(auctions, bids, tx, clock);
        // phiên số 7, người bán là user 1, giá khởi điểm 500000, bước giá 50000
        auctionStore.put(7L, Auction.restore(7L, 1L, "Đồng hồ", null, 500_000, 50_000, 500_000, null, END));
    }

    @Test
    void firstBidAtStartingPriceIsAccepted() {
        Bid bid = service.placeBid(5L, 7L, 500_000);
        assertEquals(500_000, bid.amount());
        assertEquals(5L, auctionStore.get(7L).getHighestBidderId());
    }

    @Test
    void bidBelowMinimumIsRejected() {
        service.placeBid(5L, 7L, 500_000);
        DomainException ex = assertThrows(DomainException.class, () -> service.placeBid(6L, 7L, 520_000));
        assertEquals(ErrorCode.BID_TOO_LOW, ex.getCode());
    }

    @Test
    void auctionNotFound() {
        service.placeBid(5L, 7L, 500_000);
        DomainException ex = assertThrows(DomainException.class, () -> service.placeBid(6L, 99L, 500_000));
        assertEquals(ErrorCode.AUCTION_NOT_FOUND, ex.getCode());
    }
}
