package ktpm.uet.vnu.auction.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlaceBidRequest(
        @Schema(description = "Giá đặt (VND)", example = "750000")
        @NotNull @Positive Long amount
) {
}