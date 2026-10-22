package ktpm.uet.vnu.auction.api.dto;

import jakarta.validation.constraints.*;
import java.time.Instant;

public record CreateAuctionRequest(
        @NotBlank String name,
        String description,
        @Positive long startingPrice,
        @Positive long minIncrement,
        @NotNull Instant endTime
) {
}