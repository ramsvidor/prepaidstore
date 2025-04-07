package com.ramsesvidor.prepaidstore.api;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    public Map<String, String> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");

        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            status.put("dbStatus", "UP");
        } catch (Exception e) {
            status.put("dbStatus", "DOWN");
            status.put("status", "DOWN");
        }

        return status;
    }
}
