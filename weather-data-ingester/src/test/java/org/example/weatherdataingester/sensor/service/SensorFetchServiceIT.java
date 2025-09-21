package org.example.weatherdataingester.sensor.service;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = {
                SensorFetchServiceIT.TestConfig.class,
                SensorFetchService.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
@ActiveProfiles("test")
class SensorFetchServiceIT {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private SensorFetchService service;

    @Test
    void whenRestTemplateReturnsData_thenServiceReturnsSame() {
        String url = "http://localhost:8080/sensor";
        when(restTemplate.getForObject(url, Map.class)).thenReturn(Map.of("temperature", 22));

        Map<String, Object> result = service.fetchSensorData(url);

        assertThat(result).containsEntry("temperature", 22);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        CircuitBreakerRegistry circuitBreakerRegistry() {
            return CircuitBreakerRegistry.ofDefaults();
        }

        @Bean
        RetryRegistry retryRegistry() {
            return RetryRegistry.ofDefaults();
        }
    }
}