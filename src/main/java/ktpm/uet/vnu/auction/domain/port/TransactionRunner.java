package ktpm.uet.vnu.auction.domain.port;

import java.util.function.Supplier;

/**
 * Chạy một khối lệnh trong một transaction mà tầng nghiệp vụ không cần biết
 * Spring hay JPA (tránh phải import @Transactional vào tầng domain).
 */
public interface TransactionRunner {

    <T> T execute(Supplier<T> action);

    default void run(Runnable action) {
        execute(() -> {
            action.run();
            return null;
        });
    }
}
