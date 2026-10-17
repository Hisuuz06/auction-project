package ktpm.uet.vnu.auction.domain.model;

import java.time.Instant;

/**
 * Phiên đấu giá. Java thuần.
 * Người bán là sellerId. Người thắng (khi phiên đã kết thúc) là highestBidderId.
 */
public class Auction {

    private final Long id;
    private final Long sellerId;
    private final String name;
    private final String description;
    private final long startingPrice;
    private final long minIncrement;
    private long currentPrice;
    private Long highestBidderId;
    private final Instant endTime;

    private Auction(Long id, Long sellerId, String name, String description,
                    long startingPrice, long minIncrement, long currentPrice,
                    Long highestBidderId, Instant endTime) {
        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.minIncrement = minIncrement;
        this.currentPrice = currentPrice;
        this.highestBidderId = highestBidderId;
        this.endTime = endTime;
    }

    /** Tạo phiên mới (chưa có id, chưa có bid). */
    public static Auction create(Long sellerId, String name, String description,
                                 long startingPrice, long minIncrement, Instant endTime) {
        return new Auction(null, sellerId, name, description,
                startingPrice, minIncrement, startingPrice, null, endTime);
    }

    /** Dựng lại từ dữ liệu đã lưu (dùng trong mapper của tầng dữ liệu). */
    public static Auction restore(Long id, Long sellerId, String name, String description,
                                  long startingPrice, long minIncrement, long currentPrice,
                                  Long highestBidderId, Instant endTime) {
        return new Auction(id, sellerId, name, description,
                startingPrice, minIncrement, currentPrice, highestBidderId, endTime);
    }

    /** ENDED nếu now > endTime, ngược lại ACTIVE. */
    public AuctionStatus status(Instant now) {
        return now.isAfter(endTime) ? AuctionStatus.ENDED : AuctionStatus.ACTIVE;
    }

    public boolean isEnded(Instant now) {
        return status(now) == AuctionStatus.ENDED;
    }

    public boolean hasBids() {
        return highestBidderId != null;
    }

    /** Giá tối thiểu cho lượt đặt tiếp theo (BR-3). */
    public long minNextBid() {
        return hasBids() ? currentPrice + minIncrement : startingPrice;
    }

    /** Cập nhật giá hiện tại và người dẫn đầu sau một bid hợp lệ (BR-7). */
    public void applyBid(long bidderId, long amount) {
        this.currentPrice = amount;
        this.highestBidderId = bidderId;
    }

    public Long getId() { return id; }
    public Long getSellerId() { return sellerId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public long getStartingPrice() { return startingPrice; }
    public long getMinIncrement() { return minIncrement; }
    public long getCurrentPrice() { return currentPrice; }
    public Long getHighestBidderId() { return highestBidderId; }
    public Instant getEndTime() { return endTime; }
}
