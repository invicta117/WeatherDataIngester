package org.example.weatherdataingester.sensor.service;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SensorFetchServiceTest {

    private CircuitBreakerRegistry circuitBreakerRegistry;
    private RetryRegistry retryRegistry;
    private RestTemplate restTemplate;
    private SensorFetchService service;

    @BeforeEach
    void setUp() {
        circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
        retryRegistry = RetryRegistry.ofDefaults();
        restTemplate = mock(RestTemplate.class);
        service = new SensorFetchService(restTemplate, circuitBreakerRegistry, retryRegistry);
    }

    @Test
    void whenRestTemplateSucceeds_shouldReturnResponseMap() {
        String url = "http://sensor.test/success";
        Map<String, Object> fakeResponse = Map.of("temperature", 22.5);

        when(restTemplate.getForObject(url, Map.class)).thenReturn(fakeResponse);

        Map<String, Object> result = service.fetchSensorData(url);

        assertThat(result).containsEntry("temperature", 22.5);
        verify(restTemplate).getForObject(url, Map.class);
    }

    @Test
    void whenRestTemplateFails_shouldReturnEmptyMap() {
        String url = "http://sensor.test/fail";

        when(restTemplate.getForObject(url, Map.class))
                .thenThrow(new RuntimeException("Simulated failure"));

        Map<String, Object> result = service.fetchSensorData(url);

        assertThat(result).isEmpty();
    }
}