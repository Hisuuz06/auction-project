# Auction System: hệ thống đấu giá trực tuyến

Bài tập lớn môn Kiến trúc phần mềm, **Pha 1**. Nhóm 11.

## 1. Mô tả bài toán và quy tắc nghiệp vụ của hệ thống

### 1.1. Mô tả bài toán

Trong hoạt động mua bán trực tuyến, đấu giá là hình thức cho phép nhiều người mua cạnh tranh để sở hữu một sản phẩm thông qua việc đưa ra các mức giá khác nhau. Người bán thiết lập giá khởi điểm, bước giá tối thiểu và thời hạn đấu giá. Khi phiên kết thúc, người đưa ra mức giá hợp lệ cao nhất sẽ trở thành người thắng.

Nhóm xây dựng hệ thống Backend đấu giá trực tuyến (Online Auction Backend) cung cấp các REST API cho phép người dùng đăng ký, đăng nhập, tạo phiên đấu giá, tham gia đặt giá, theo dõi lịch sử và xem kết quả đấu giá.

Hệ thống không phân chia cố định vai trò người mua và người bán. Một người dùng có thể tạo phiên đấu giá của mình và tham gia đặt giá ở các phiên do người khác tạo.

### 1.2. Chức năng và quy tắc nghiệp vụ

#### 1.2.1 Các chức năng chính:

+ Quản lý tài khoản: Đăng ký, đăng nhập và xác thực người dùng.

+ Quản lý phiên đấu giá: Tạo, xem danh sách, xem chi tiết và xóa phiên khi đủ điều kiện.

+ Đặt giá: Người dùng đã đăng nhập có thể tham gia đặt giá trong các phiên của người khác.

+ Lịch sử đấu giá: Xem danh sách các lượt đặt giá của từng phiên.

+ Kết quả đấu giá: Xác định người thắng khi phiên kết thúc.

#### 1.2.2 Các quy tắc nghiệp vụ:

+ Khách chưa đăng nhập chỉ được xem danh sách, chi tiết và lịch sử đặt giá.

+ Giá đặt đầu tiên phải lớn hơn hoặc bằng giá khởi điểm. Các lượt tiếp theo phải lớn hơn hoặc bằng giá hiện tại + bước giá tối thiểu.

+ Chỉ được đặt giá khi phiên còn thời hạn và người bán không được đặt giá trong phiên của mình.

+ Chỉ chủ phiên mới được xóa phiên và chỉ khi chưa có lượt đặt giá nào.

+ Trạng thái ACTIVE hoặc ENDED được xác định dựa trên endTime, không lưu trực tiếp trong cơ sở dữ liệu.

+ Người thắng là người dẫn đầu khi phiên kết thúc. Nếu không có lượt đặt giá nào thì phiên không có người thắng.

## 2. Công nghệ

| Thành phần | Lựa chọn |
|---|---|
| Ngôn ngữ, framework | Java 17, Spring Boot 3.3 |
| Cơ sở dữ liệu | MySQL 8, truy cập qua JPA/Hibernate |
| Xác thực | JWT (jjwt), mật khẩu băm BCrypt |
| Tài liệu API | OpenAPI/Swagger (springdoc) |
| Đóng gói | Docker, docker compose |
| Kiểm thử | JUnit 5, Mockito; Locust cho kiểm thử tải |

## 3. Kiến trúc

Hệ thống chia 3 tầng: **API → Nghiệp vụ → Truy cập dữ liệu**. Tầng nghiệp vụ là Java thuần, không import Spring hay JPA.

```mermaid
flowchart LR
    C[Client / Swagger UI] --> A
    subgraph app[Ứng dụng Spring Boot]
        A["API<br/>controller, DTO, interceptor JWT"] --> D["Nghiệp vụ (domain)<br/>service, model, port"]
        I["Hạ tầng<br/>JPA entity, adapter, JWT, BCrypt"] -. cài đặt các port .-> D
    end
    I --> DB[(MySQL)]
```

```
ktpm.uet.vnu.auction
├── api/              controller, dto, security (interceptor JWT), error (xử lý lỗi chung)
├── domain/           model, service, port (interface repository), exception
└── infrastructure/   persistence (entity, Jpa repository, adapter), security, config
```

Các quyết định thiết kế:

- **Port và adapter:** tầng nghiệp vụ chỉ biết các interface `AuctionRepository`, `BidRepository`... Bản cài đặt JPA nằm ở tầng hạ tầng và đổi giữa entity với model thuần. Test `DomainLayerPurityTest` tự động kiểm tra tầng domain không import framework.
- **Transaction:** nghiệp vụ gọi `TransactionRunner` thay vì `@Transactional`, nên không phụ thuộc Spring.
- **Xử lý lỗi:** mọi lỗi nghiệp vụ là `DomainException` kèm `ErrorCode`; `GlobalExceptionHandler` đổi thành JSON thống nhất, controller không có `try/catch`.

## 4. Đặc tả

### 4.1 Cơ sở dữ liệu

```mermaid
erDiagram
    USERS ||--o{ AUCTIONS : "bán"
    USERS |o--o{ AUCTIONS : "đang dẫn đầu"
    AUCTIONS ||--o{ BIDS : "nhận"
    USERS ||--o{ BIDS : "đặt"

    USERS {
        bigint id PK
        varchar username UK
        varchar password "BCrypt"
    }
    AUCTIONS {
        bigint id PK
        bigint seller_id FK
        varchar name
        text description
        bigint starting_price
        bigint min_increment
        bigint current_price
        bigint highest_bidder_id FK
        timestamp end_time
    }
    BIDS {
        bigint id PK
        bigint auction_id FK
        bigint bidder_id FK
        bigint amount
        timestamp created_at
    }
```

### 4.2 API

Đặc tả OpenAPI: [docs/openapi.yaml](docs/openapi.yaml)
· Xem trực quan: [Swagger Editor](https://editor.swagger.io/?url=https://raw.githubusercontent.com/Hisuuz06/auction-project/main/docs/openapi.yaml)

Khi ứng dụng đang chạy, cùng nội dung có ở Swagger UI (`/swagger-ui.html`) và `/v3/api-docs.yaml`.

| # | Method | Endpoint | Mô tả | Cần token |
|---|---|---|---|---|
| 1 | POST | `/api/auth/register` | Đăng ký | Không |
| 2 | POST | `/api/auth/login` | Đăng nhập, nhận JWT | Không |
| 3 | GET | `/api/auctions` | Danh sách phiên | Không |
| 4 | GET | `/api/auctions/{id}` | Chi tiết phiên | Không |
| 5 | POST | `/api/auctions` | Tạo phiên | Có |
| 6 | DELETE | `/api/auctions/{id}` | Xóa phiên | Có |
| 7 | GET | `/api/auctions/{id}/bids` | Lịch sử đặt giá | Không |
| 8 | POST | `/api/auctions/{id}/bids` | Đặt giá | Có |
| 9 | GET | `/api/users/me/won-auctions` | Các phiên đã thắng | Có |

Request và response chi tiết, mã lỗi: xem [docs/api.md](docs/api.md) hoặc Swagger UI. Lỗi trả về dạng `{timestamp, status, code, message, path}`, ví dụ `BID_TOO_LOW` (409), `SELLER_CANNOT_BID` (403), `AUCTION_ENDED` (409).

## 5. Bảo mật

- Đăng nhập trả JWT (hạn 1 giờ). Mật khẩu lưu dạng băm BCrypt.
- Một **interceptor** kiểm tra token một lần cho các endpoint cần xác thực, rồi gắn `userId` vào request; controller chỉ đọc `userId`, không tự xử lý token.
- Endpoint công khai: đăng ký, đăng nhập, xem danh sách, chi tiết phiên, lịch sử đặt giá và Swagger. Còn lại cần `Authorization: Bearer <token>`.
- Việc "có phải người bán không" là quy tắc nghiệp vụ, kiểm tra ở tầng nghiệp vụ bằng cách so `userId` với dữ liệu phiên.

## 6. Chạy dự án

Cần Docker. Từ thư mục gốc:

```bash
docker compose up -d --build
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- Khi mới chạy, cơ sở dữ liệu **trống**. Thử nhanh trên Swagger:
    1. `POST /api/auth/register` tạo hai tài khoản, ví dụ `seller1` và `buyer1` (mật khẩu từ 8 ký tự).
    2. `POST /api/auth/login` với `seller1`, copy `token`, bấm **Authorize** và dán token (không gõ chữ `Bearer`).
    3. `POST /api/auctions` để tạo một phiên.
    4. Đăng nhập `buyer1`, bấm **Authorize** lại (Logout rồi dán token mới), rồi `POST /api/auctions/{id}/bids` để đặt giá.
- Dừng: `docker compose down` (thêm `-v` để xóa dữ liệu).

Chạy test: `mvn clean verify` (cần JDK 17).

## 7. Kiểm thử

- **Test tự động:** Gồm luật đặt giá (`BidServiceTest`), phiên đấu giá, xác thực, test controller (`@WebMvcTest`) và test kiểm tra phân tầng.
- **Kịch bản chạy tay trên Swagger:** Tất cả đạt (đặt giá hợp lệ, giá quá thấp, người bán tự đặt giá, phiên đã kết thúc, không token, token sai...).

## 8. Kiểm thử tải (Kaggle CPU)

Notebook:https://www.kaggle.com/code/hieukid/ki-m-t-i-u-gi. Chạy toàn bộ trên một notebook Kaggle để cấu hình phần cứng cố định; app và công cụ tải dùng chung CPU.

### 8.1. Môi trường kiểm thử

Nhóm sử dụng Locust để kiểm thử tải các REST API trên môi trường Kaggle CPU.

| Thông số | Giá trị |
|---|---|
| Phần cứng | Intel Xeon 2.20 GHz, 4 CPU logic, RAM 31 GiB |
| Công nghệ | Java 17, Spring Boot 3.3.5, MySQL 8 |
| Công cụ | Locust (Python) |
| Dữ liệu | 100 phiên đấu giá mẫu cho GET; dữ liệu thử nghiệm riêng cho POST |
| Mức tải | 10, 30, 50 người dùng ảo |
| Thời gian | Warm-up 15 giây, đo 60 giây/mức tải |

Spring Boot, MySQL và Locust chạy trên cùng môi trường Kaggle CPU.

### 8.2. Kết quả kiểm thử Baseline

Kết quả tại mức tải **50 người dùng ảo**:

| API | RPS | Avg (ms) | p95 (ms) | Error Rate |
|---|---:|---:|---:|---:|
| GET /api/auctions | 32,33 | 4,15 | 6 | 0% |
| GET /api/auctions/{id} | 98,64 | 3,81 | 5 | 0% |
| POST /api/auctions | 127,38 | 28,49 | 45 | 0% |
| POST /api/auctions/{id}/bids | 119,97 | 52,82 | 97 | 0% |

RPS: request/giây; 
TB: độ trễ trung bình; 
p95: ngưỡng thời gian phản hồi mà 95% request không vượt quá. 

Tổng cộng ba bài kiểm thử tải thông thường thực hiện
**40.940 request**, không ghi nhận request thất bại theo Locust.

Nhóm thực hiện thêm kiểm thử nhiều người dùng đồng thời đặt cùng một mức giá
trên một phiên đấu giá. Theo quy tắc nghiệp vụ, tối đa một lượt đặt giá
ở mức giá đó được chấp nhận.

| Người dùng đồng thời | Số vòng kiểm thử | Số bid được chấp nhận mỗi vòng | Vòng vi phạm |
|---|---:|---:|---:|
| 10 | 5 | 2 | 5/5 |
| 30 | 5 | 2–3 | 5/5 |
| 50 | 5 | 2 | 5/5 |
| **Tổng** | **15** | | **15/15** |

**Kết quả:** Cả 15/15 vòng kiểm thử đều vi phạm bất biến nghiệp vụ.
Hệ thống chấp nhận nhiều lượt đặt giá cùng mức giá trên một phiên,
cho thấy dấu hiệu Race Condition khi xử lý các request đồng thời.

### 8.3. Nhận xét và đánh giá

- **Hiệu năng:** Thông lượng tăng khi số người dùng tăng từ 10 lên 50.
  Hệ thống đáp ứng ổn định trong phạm vi tải đã kiểm thử.
- **API GET:** Thời gian phản hồi thấp, trung bình dưới 5 ms.
  GET chi tiết có RPS cao hơn do được gọi nhiều hơn trong kịch bản.
- **API POST:** POST Bid có thời gian phản hồi cao nhất (52,82 ms),
  liên quan đến xử lý nghiệp vụ và cập nhật dữ liệu.
- **Độ ổn định:** Tỷ lệ request thất bại theo Locust là 0%
  trong các bài kiểm thử tải thông thường.
- **Race Condition:** Kiểm thử đặt giá đồng thời phát hiện 15/15 vòng
  vi phạm bất biến nghiệp vụ, có trường hợp 2–3 bid cùng giá
  được chấp nhận.
- **Hướng cải tiến:** Nghiên cứu Optimistic/Pessimistic Locking,
  kiểm thử lại và so sánh hiệu năng trước–sau.

**Kết luận:** Hệ thống đáp ứng ổn định các mức tải đã thử nghiệm
nhưng còn tồn tại vấn đề về tính nhất quán khi đặt giá đồng thời.

## 9. Hạn chế và hướng Pha 2

- **Xử lý đồng thời khi đặt giá:** chưa có khóa nên hai người đặt cùng lúc có thể cùng qua kiểm tra.
- **`GET /api/auctions` trả toàn bộ danh sách** (chưa phân trang), chậm khi dữ liệu lớn.
- **Truy vấn theo người dùng** (`won-auctions`) chưa có index phù hợp.
- Hướng cải tiến:

| Vấn đề | Thuộc tính | Cải tiến | Chỉ số đo trước và sau |
|---|---|---|---|
| Đặt giá đồng thời | Nhất quán dữ liệu | Khóa lạc quan (`@Version`) kèm thử lại | Số phiên lệch giá và bid trùng giá (mong đợi 0); thông lượng đặt giá |
| Danh sách không phân trang | Hiệu năng | Phân trang `page`, `size` | p95 và kích thước response của `GET /api/auctions` |
| Đọc nhiều, xuống DB mọi lần | Hiệu năng, mở rộng | Cache chi tiết phiên, xóa khi có bid mới | RPS và p95 của các `GET` |
| Thiếu chỉ mục kết hợp | Hiệu năng | Chỉ mục cho `won-auctions` và lịch sử bid | p95 của hai endpoint đó |

## 10. Thành viên

| Họ tên            | Phụ trách                                                    |
|-------------------|--------------------------------------------------------------|
| Nguyễn Ngọc Thắng | Xác thực, JWT, test                                          |
| Đào Trung Hiếu    | Phiên đấu giá, test, README (chạy, kiểm thử kaggle)          |
| Lê Trung Hiếu     | Đặt giá, Swagger, Docker, test, README (kiến trúc, ERD, API) |
