// health is one endpoint to prove the whole request → code → response chain works.
package com.maryam.masar.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //this class handles web requests and returns data directly
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "Masar API is running";
    }
}