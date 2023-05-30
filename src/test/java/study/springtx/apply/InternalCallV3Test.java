package study.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV3Test {

    @Autowired
    CallServie callServie;

    @Test
    void printProxy() {
        log.info("callService class={}", callServie.getClass());
    }

    @Test
    void internalCall() {
        callServie.internal();
    }

    @Test
    void externalCall() {
        callServie.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig {
        @Bean
        CallServie callServie() {
            return new CallServie();
        }
    }

    @Slf4j
    static class CallServie {

        @Autowired
        private ObjectProvider<CallServie> selfProvider;

        public void external() {
            log.info("call external");
            printTxInfo();
            CallServie self = selfProvider.getObject();
            log.info("self class={}", self.getClass());
            self.internal();
        }

        @Transactional
        public void internal() {
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            log.info("tx readOnly={}", readOnly);
        }
    }
}
