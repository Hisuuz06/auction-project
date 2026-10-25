package ktpm.uet.vnu.auction.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ktpm.uet.vnu.auction.api.dto.BidResponse;
import ktpm.uet.vnu.auction.api.dto.PlaceBidRequest;
import ktpm.uet.vnu.auction.domain.exception.DomainException;
import ktpm.uet.vnu.auction.domain.exception.ErrorCode;
import ktpm.uet.vnu.auction.domain.model.Bid;
import ktpm.uet.vnu.auction.domain.service.BidService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions/{auctionId}/bids")
@Tag(name = "Bids")
public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @GetMapping
    @Operation(summary = "Lịch sử bid của một phiên (giá cao nhất trước)")
    public List<BidResponse> list(@PathVariable long auctionId) {
        return bidService.listByAuction(auctionId).stream()
                .map(BidResponse::from)
                .toList();
    }

    @PostMapping
    @Operation(summary = "Đặt giá cho một phiên (cần đăng nhập)")
    public ResponseEntity<BidResponse> place(@PathVariable long auctionId,
                                             @Valid @RequestBody PlaceBidRequest body,
                                             HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            // Interceptor của TV1 luôn gắn userId; dòng này chỉ để trả 401 thay vì lỗi 500 nếu thiếu
            throw new DomainException(ErrorCode.UNAUTHORIZED, "Thiếu hoặc sai token");
        }
        Bid bid = bidService.placeBid((Long) userId, auctionId, body.amount());
        return ResponseEntity.status(HttpStatus.CREATED).body(BidResponse.from(bid));
    }
}