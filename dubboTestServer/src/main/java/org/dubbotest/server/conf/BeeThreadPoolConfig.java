package org.dubbotest.server.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class BeeThreadPoolConfig {
    @Bean(name = "itemFeatureExecutor")
    public ExecutorService itemFeatureExecutor() {
        return new ThreadPoolExecutor(
                10,
                30,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10), // 有界队列，容量1000
                new CustomThreadFactory("itemFeatureThread"),
                new ThreadPoolExecutor.AbortPolicy()
        );
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
