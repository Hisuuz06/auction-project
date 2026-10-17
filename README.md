# auction-system 
Backend đấu giá trực tuyến, Spring Boot 3 / Java 17 / MySQL.

## Kiến trúc 3 tầng

```
ktpm.uet.vnu.auction
├── api/                      TẦNG API
│   ├── controller/           REST controller
│   ├── dto/                  request/response DTO
│   ├── security/             interceptor/filter JWT
│   └── error/                ErrorResponse, ErrorStatus, GlobalExceptionHandler  
├── domain/                   TẦNG NGHIỆP VỤ (Java thuần)
│   ├── model/                User, Auction, Bid, AuctionStatus  
│   ├── service/              AuthService, AuctionService, BidService
│   ├── port/                 interface repository + PasswordHasher, TokenService, TransactionRunner
│   └── exception/            ErrorCode, DomainException
└── infrastructure/           TẦNG DỮ LIỆU và hạ tầng (JPA, jjwt, BCrypt)
    ├── persistence/entity/       JPA entity   (AuctionEntity ...)
    ├── persistence/repository/   Spring Data  (AuctionJpaRepository ...)
    ├── persistence/adapter/      cài đặt port + map entity <-> domain model
    ├── security/                 BCryptPasswordHasher, JwtTokenService
    └── config/                   khai báo bean
```

Hướng phụ thuộc: `api` -> `domain` <- `infrastructure`. Domain không biết api và infrastructure.
`DomainLayerPurityTest` quét mã nguồn domain và fail nếu có import bị cấm (đề bài yêu cầu điểm này).

## Quy ước

- Cổng (port) ở domain tên `AuctionRepository`; bản Spring Data tên `AuctionJpaRepository`; adapter tên `AuctionRepositoryAdapter`.
- Controller không có luật nghiệp vụ, không try/catch. Lỗi nghiệp vụ ném `DomainException(ErrorCode, message)`, `GlobalExceptionHandler` đổi thành JSON.
- Cần transaction (đặt giá = lưu bid + cập nhật phiên): dùng `TransactionRunner.execute(...)`, không dùng `@Transactional` trong domain.
- Tầng API điền trường `status` của phiên bằng `AuctionService.statusOf(auction)`.
- Mỗi người một class cấu hình bean để không chờ nhau, ví dụ:

```java
@Configuration
public class AuctionBeanConfig {
    @Bean
    AuctionService auctionService(AuctionRepository auctions, Clock clock) {
        return new AuctionService(auctions, clock);
    }
}
```

## Chạy

```bash
docker compose up --build        # MySQL + app, Swagger tại http://localhost:8080/swagger-ui.html
mvn clean verify                 # build và chạy test (ngày 1: chạy ngay để bắt lỗi biên dịch)
```

Ứng dụng chỉ khởi động được khi các cổng (UserRepository, AuctionRepository, BidRepository,
PasswordHasher, TokenService) đã có bản cài đặt và bean đã được khai báo.
