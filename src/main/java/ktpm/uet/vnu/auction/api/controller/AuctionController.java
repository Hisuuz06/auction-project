package ktpm.uet.vnu.auction.api.controller;
import ktpm.uet.vnu.auction.domain.model.Auction;
import ktpm.uet.vnu.auction.domain.service.AuctionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import ktpm.uet.vnu.auction.api.dto.CreateAuctionRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;

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

    // POST/api/auctions
    @PostMapping
    public ResponseEntity<AuctionResponse> create(
            @Valid @RequestBody CreateAuctionRequest request,
            @RequestAttribute("userId") Long sellerId) {

        Auction auction = auctionService.create(
                sellerId,
                request.name(),
                request.description(),
                request.startingPrice(),
                request.minIncrement(),
                request.endTime()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(auction));
    }
    // DELETE/api/auctions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable long id,
            @RequestAttribute("userId") Long requesterId) {

        auctionService.delete(requesterId, id);

        return ResponseEntity.noContent().build();
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
