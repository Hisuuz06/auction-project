package ktpm.uet.vnu.auction.domain.exception;

/** Lỗi nghiệp vụ duy nhất của hệ thống, mang theo mã lỗi. */
public class DomainException extends RuntimeException {

    private final ErrorCode code;

    public DomainException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
