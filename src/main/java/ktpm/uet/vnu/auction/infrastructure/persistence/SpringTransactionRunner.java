package ktpm.uet.vnu.auction.infrastructure.persistence;

import ktpm.uet.vnu.auction.domain.port.TransactionRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/** Bản cài đặt TransactionRunner bằng Spring: tầng domain không phải import @Transactional. */
@Component
public class SpringTransactionRunner implements TransactionRunner {

    private final TransactionTemplate template;

    public SpringTransactionRunner(PlatformTransactionManager transactionManager) {
        this.template = new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T execute(Supplier<T> action) {
        return template.execute(status -> action.get());
    }
}
