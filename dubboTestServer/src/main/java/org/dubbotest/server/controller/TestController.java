package org.dubbotest.server.controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.dubbotest.server.service.BusinessServiceImpl;
import org.dubbotest.server.service.TestPrometheusServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

import static org.dubbotest.server.utils.CommonUtils.randomSleep;

@RestController
@RefreshScope
public class TestController {
    @Value("${profile.item.parallel:2}") // 假设你的配置中有这个key
    private volatile int testValue;

    @Autowired
    private TestPrometheusServiceImpl testPrometheusServiceImpl;

    @GetMapping("/test")
    @Timed(value = "test.getTestValue", description = "getTestValue", percentiles = {0.5, 0.95, 0.99})
    @Counted(value = "test.getTestValue", description = "getTestValue")
    public int getTestValue(String name) {
        randomSleep();

        return testValue;
    }

    @GetMapping("/prometheus/test")
    public String testPrometheus() {
        for (int i = 0; i < 1000; i++) {
            String featureName = "feature" + i % 10;
            testPrometheusServiceImpl.testMeter(new TestPrometheusServiceImpl.FeatureConf(featureName, "test"));
            testPrometheusServiceImpl.testMeterAnnotation(new TestPrometheusServiceImpl.FeatureConf(featureName, "test"));
        }

        return "testMeter finished";
    }

    @GetMapping("/newobj")
    public String testNewObj() throws ExecutionException, InterruptedException {
        BusinessServiceImpl businessService = new BusinessServiceImpl();
        return businessService.testNewObj();
    }

}
