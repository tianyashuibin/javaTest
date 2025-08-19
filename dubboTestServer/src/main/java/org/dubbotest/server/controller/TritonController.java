package org.dubbotest.server.controller;

import org.dubbotest.server.service.TritonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class TritonController {
    @Autowired
    private TritonServiceImpl tritonReqService;
    @GetMapping("/triton")
    public String triton() {
        return tritonReqService.predict();
    }
}
