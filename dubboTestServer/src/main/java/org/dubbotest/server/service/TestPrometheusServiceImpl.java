package org.dubbotest.server.service;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.dubbotest.server.utils.CommonUtils.randomSleep;

@Service
public class TestPrometheusServiceImpl {
    @Autowired
    private MeterRegistry registry;

//    @Counted(value = "reco.api.bigdataReco", description = "testCount")
//    @Timed( value = "reco.api.bigdataReco", description = "testMeter", percentiles = {0.5, 0.95, 0.99}, extraTags = {"feature_name", "#conf.name"})
    public void testMeter(FeatureConf conf) {
        Timer.Sample sample = Timer.start(registry);

        Counter counter = Counter.builder("reco.api.bigdataReco.req")
                .description("testCount")
                .tag("feature_name", conf.name)
                .register(registry);
        counter.increment();

        // 这个方法和上面的方法效果一样，但是更简洁
        registry.counter("reco.api.bigdataReco.req", "feature_name", conf.name).increment();

        Timer timer = Timer.builder("reco.api.bigdataReco")
                .description("testMeter")
                .tag("feature_name", conf.name)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);

        timer.record(() -> {
            randomSleep();
        });

        sample.stop(Timer.builder("reco.api.bigdataReco.new").tag("feature_name", conf.name).register(registry));
    }

    // 注解的方式没有办法传入函数参数，传入的实际是一个字符串 "#{conf.name}"
    // 使用参数中的tag，还是需要写Timer的方式来实现，如上面testMeter例子所示
    @Counted(value = "reco.api.annotation", description = "testCount")
    @Timed(value = "reco.api.annotation", percentiles = {0.5, 0.95, 0.99}, extraTags = {"feature_name", "#{conf.name}"})
    public void testMeterAnnotation(FeatureConf conf) {

    }

    @Data
    @AllArgsConstructor
    public static class FeatureConf {
        private String name;
        private String value;
    }
}
