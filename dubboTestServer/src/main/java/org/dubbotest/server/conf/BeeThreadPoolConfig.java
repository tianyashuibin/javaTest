package org.dubbotest.server.conf;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class BeeThreadPoolConfig {
    @Autowired
    private MeterRegistry meterRegistry;
    @Bean(name = "itemFeatureExecutor")
    public ExecutorService itemFeatureExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10,
                30,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10), // 有界队列，容量1000
                new CustomThreadFactory("itemFeatureThread"),
                new ThreadPoolExecutor.AbortPolicy()
        );
        return ExecutorServiceMetrics.monitor(meterRegistry, executor, "itemFeatureThread");
    }

    static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private String NAME_PREFIX;

        public CustomThreadFactory(String namePrefix) {
            NAME_PREFIX = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(NAME_PREFIX + "-" + threadNumber.getAndIncrement());
            return thread;
        }
    }
}
