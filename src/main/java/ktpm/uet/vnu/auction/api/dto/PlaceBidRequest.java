package ktpm.uet.vnu.auction.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlaceBidRequest(
        @NotNull @Positive Long amount
) {
}