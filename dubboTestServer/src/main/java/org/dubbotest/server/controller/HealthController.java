package org.dubbotest.server.controller;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class HealthController {
    @GetMapping("/health/check")
    public String healthCheck() {
        return "OK";
    }
}
