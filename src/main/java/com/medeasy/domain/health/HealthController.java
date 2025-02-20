package com.medeasy.domain.health;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open-api/health")
public class HealthController {

    @GetMapping("")
    @Operation(summary = "heath test")
    public String health() {
        return "OK";
    }
}
