package org.dubbotest.server.provider;

import org.apache.dubbo.config.annotation.DubboService;
import org.dubbotest.api.TestDubboService;

@DubboService
public class TestDubboServiceImpl implements TestDubboService {
    @Override
    public String getTestValue() {
        return "dubbo rpc test";
    }
}
