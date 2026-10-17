package ktpm.uet.vnu.auction.api.error;

import ktpm.uet.vnu.auction.domain.exception.DomainException;
import ktpm.uet.vnu.auction.domain.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/** Biến mọi lỗi thành JSON theo định dạng chung. Controller không cần try/catch. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(DomainException ex, HttpServletRequest request) {
        return build(ex.getCode(), ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Dữ liệu không hợp lệ");
        return build(ErrorCode.VALIDATION_ERROR, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        return build(ErrorCode.VALIDATION_ERROR, "Nội dung JSON không hợp lệ", request);
    }

    private ResponseEntity<ErrorResponse> build(ErrorCode code, String message, HttpServletRequest request) {
        int status = ErrorStatus.of(code);
        ErrorResponse body = new ErrorResponse(Instant.now(), status, code.name(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
