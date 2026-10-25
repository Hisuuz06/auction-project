package ktpm.uet.vnu.auction.api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ktpm.uet.vnu.auction.domain.exception.DomainException;
import ktpm.uet.vnu.auction.domain.exception.ErrorCode;
import ktpm.uet.vnu.auction.domain.port.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public JwtAuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Cho qua request OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Bỏ qua các endpoint công khai theo spec
        if (isPublicEndpoint(request)) {
            return true;
        }

        // Kiểm tra header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new DomainException(ErrorCode.UNAUTHORIZED, "Yêu cầu đăng nhập: thiếu token hoặc sai định dạng");
        }

        String token = authHeader.substring(7);
        Optional<Long> userIdOpt = tokenService.verify(token);

        if (userIdOpt.isEmpty()) {
            throw new DomainException(ErrorCode.UNAUTHORIZED, "Token không hợp lệ hoặc đã hết hạn");
        }

        // Lưu userId vào request đúng theo quy ước nhóm đã chốt ở Ngày 1
        request.setAttribute("userId", userIdOpt.get());
        return true;
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 1. Auth endpoints
        if (uri.startsWith("/api/auth/")) {
            return true;
        }

        // 2. Swagger UI & API docs
        if (uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs") || uri.equals("/error")) {
            return true;
        }

        // 3. Các GET công khai (GET /api/auctions, GET /api/auctions/{id}, GET /api/auctions/{id}/bids)
        // Lưu ý: GET /api/users/me/won-auctions là private (không nằm trong danh sách này)
        if ("GET".equalsIgnoreCase(method)) {
            if (uri.equals("/api/auctions") || uri.matches("^/api/auctions/\\d+$") || uri.matches("^/api/auctions/\\d+/bids$")) {
                return true;
            }
        }

        return false;
    }
}
