package org.dubbotest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 *
 */
public class App 
{
    static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("itemFeatureThread-" + threadNumber.getAndIncrement());
            return thread;
        }
    }

    private static final ExecutorService itemFeatureExecutor = new ThreadPoolExecutor(
            50000,
            50000,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000), // 有界队列，容量1000
            new CustomThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public static void main( String[] args )
    {
        Long start = System.currentTimeMillis();
        System.out.println( "Hello World!" );
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ConcurrentMap<String, String> collect = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    processFeature(finalI, collect, ids);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }, itemFeatureExecutor));
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(1, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        System.out.println(collect);
        System.out.println("cost: " + (System.currentTimeMillis() - start));
    }

    private static void processFeature(Integer featureConf, ConcurrentMap<String, String> collect, List<Integer> recallBeans) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " processFeature: " + featureConf);
//        for (Integer id : recallBeans) {
//            System.out.println("processFeature: " + featureConf + " id: " + id);
//        }
//        Thread.sleep(100);
        collect.put(featureConf.toString(), featureConf.toString());
    }
}
