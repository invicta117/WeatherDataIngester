package org.example.weatherdataingester.sensor.service;

import org.example.weatherdataingester.metric.repository.MetricValueRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class SensorSchedulerServiceIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    @Autowired
    private SensorSchedulerService schedulerService;
    @Autowired
    private MetricValueRepository metricValueRepository;
    @MockBean
    private SensorFetchService sensorFetchService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void fetchWeatherData_shouldSaveMetricsToDatabase() {
        Map<String, Object> response = Map.of(
                "humidity", 55.0
        );
        Mockito.when(sensorFetchService.fetchSensorData(Mockito.anyString()))
                .thenReturn(response);

        schedulerService.fetchWeatherData();

        var results = metricValueRepository.findAll();
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getType().name()).isEqualTo("HUMIDITY");
        assertThat(results.get(0).getValue()).isEqualTo(55.0);
    }
}
