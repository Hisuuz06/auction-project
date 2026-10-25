
package ktpm.uet.vnu.auction.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import ktpm.uet.vnu.auction.domain.model.Auction;
import ktpm.uet.vnu.auction.domain.service.AuctionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me")
public class MeController {

    private final AuctionService auctionService;

    public MeController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    // GET /api/users/me/won-auctions
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/won-auctions")
    public ResponseEntity<List<AuctionController.AuctionResponse>>
    getWonAuctions(@RequestAttribute("userId") Long userId) {

        List<AuctionController.AuctionResponse> response =
                auctionService.listWonBy(userId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // Chuyển Auction sang Response DTO
    private AuctionController.AuctionResponse toResponse(
            Auction auction) {

        return new AuctionController.AuctionResponse(
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
}
