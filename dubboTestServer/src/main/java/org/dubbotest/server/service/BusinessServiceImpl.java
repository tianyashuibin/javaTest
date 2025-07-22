package org.dubbotest.server.service;

import org.dubbotest.server.utils.BeeSpringContextHolder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class BusinessServiceImpl {
    private static final ExecutorService itemFeatureExecutor;

    static {
        itemFeatureExecutor = (ExecutorService) BeeSpringContextHolder.getBean("itemFeatureExecutor");
    }

    public String testNewObj() throws ExecutionException, InterruptedException {
        itemFeatureExecutor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("testNewObjRunnable");
            }
        });

        Future<String> future = itemFeatureExecutor.submit(() -> {
            return "testNewObj";
        });

        return future.get();
    }
}
