package ktpm.uet.vnu.auction.api.controller;

import ktpm.uet.vnu.auction.api.security.JwtAuthInterceptor;
import ktpm.uet.vnu.auction.api.security.WebMvcConfig;
import ktpm.uet.vnu.auction.domain.exception.DomainException;
import ktpm.uet.vnu.auction.domain.exception.ErrorCode;
import ktpm.uet.vnu.auction.domain.model.Bid;
import ktpm.uet.vnu.auction.domain.port.TokenService;
import ktpm.uet.vnu.auction.domain.service.BidService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = BidController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthInterceptor.class, WebMvcConfig.class}))
class BidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BidService bidService;

    @MockBean
    private TokenService tokenService;

    @Test
    void placeBidReturns201() throws Exception {
        when(bidService.placeBid(2L, 7L, 750_000L))
                .thenReturn(new Bid(
                        21L,
                        7L,
                        2L,
                        750_000L,
                        Instant.parse("2026-10-21T09:15:00Z")
                ));

        mockMvc.perform(post("/api/auctions/7/bids")
                        .requestAttr("userId", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":750000}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(21))
                .andExpect(jsonPath("$.amount").value(750000));
    }

    @Test
    void bidTooLowReturns409WithCode() throws Exception {
        when(bidService.placeBid(2L, 7L, 100L))
                .thenThrow(new DomainException(
                        ErrorCode.BID_TOO_LOW,
                        "Giá đặt phải từ 750000 trở lên"
                ));

        mockMvc.perform(post("/api/auctions/7/bids")
                        .requestAttr("userId", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("BID_TOO_LOW"));
    }

    @Test
    void missingAmountReturns400() throws Exception {
        mockMvc.perform(post("/api/auctions/7/bids")
                        .requestAttr("userId", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}