package org.example.weatherdataingester.sensor.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Service
public class SensorFetchService {

    private final RestTemplate restTemplate;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    public SensorFetchService(RestTemplate restTemplate, CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry) {
        this.restTemplate = restTemplate;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
    }

    public Map<String, Object> fetchSensorData(String url) {
        String breakerName = "sensor-" + url.hashCode();

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(breakerName, "default");
        Retry retry = retryRegistry.retry(breakerName, "default");

        Supplier<Map<String, Object>> decoratedSupplier =
                CircuitBreaker.decorateSupplier(circuitBreaker,
                        () -> restTemplate.getForObject(url, Map.class));

        Supplier<Map<String, Object>> withRetry = Retry.decorateSupplier(retry, decoratedSupplier);

        return Try.ofSupplier(withRetry)
                .recover(throwable -> {
                    log.warn("Circuit breaker OPEN or failure for {}: {}", url, throwable.toString());
                    return Collections.emptyMap();
                })
                .get();
    }
}