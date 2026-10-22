
package ktpm.uet.vnu.auction.api.controller;

import ktpm.uet.vnu.auction.domain.model.Auction;
import ktpm.uet.vnu.auction.domain.service.AuctionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    // GET /api/auctions
    @GetMapping
    public ResponseEntity<List<AuctionResponse>> list() {

        List<AuctionResponse> response = auctionService.list()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // GET /api/auctions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> get(
            @PathVariable long id) {

        Auction auction = auctionService.get(id);

        return ResponseEntity.ok(toResponse(auction));
    }

    // Chuyển Domain Model thành JSON Response
    private AuctionResponse toResponse(Auction auction) {
        return new AuctionResponse(
                auction.getId(),
                auction.getSellerId(),
                auction.getName(),
                auction.getDescription(),
                auction.getStartingPrice(),
                auction.getMinIncrement(),
                auction.getCurrentPrice(),
                auction.getHighestBidderId(),
                auction.getEndTime(),
                auctionService.statusOf(auction).name()
        );
    }

    public record AuctionResponse(
            Long id,
            Long sellerId,
            String name,
            String description,
            long startingPrice,
            long minIncrement,
            long currentPrice,
            Long highestBidderId,
            Instant endTime,
            String status
    ) {}
}
