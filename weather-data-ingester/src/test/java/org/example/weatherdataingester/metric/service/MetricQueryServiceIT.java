package org.example.weatherdataingester.metric.service;

import org.example.common.model.MetricType;
import org.example.common.model.StatisticType;
import org.example.weatherdataingester.metric.dto.MetricQueryRequest;
import org.example.weatherdataingester.metric.entity.MetricValue;
import org.example.weatherdataingester.sensor.entity.Sensor;
import org.example.weatherdataingester.metric.repository.MetricValueRepository;
import org.example.weatherdataingester.sensor.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test") // -> loads application-test.properties
class MetricQueryServiceIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MetricQueryService metricQueryService;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private MetricValueRepository metricValueRepository;

    private Sensor sensor;

    @BeforeEach
    void setUp() {
        metricValueRepository.deleteAll();
        sensorRepository.deleteAll();

        sensor = sensorRepository.save(new Sensor(1L, "Test Sensor"));

        metricValueRepository.save(new MetricValue(null, sensor, MetricType.TEMPERATURE, 20.5, Instant.now()));
        metricValueRepository.save(new MetricValue(null, sensor, MetricType.TEMPERATURE, 25.5, Instant.now()));
        metricValueRepository.save(new MetricValue(null, sensor, MetricType.HUMIDITY, 60.0, Instant.now()));
    }

    @Test
    void shouldAggregateMetricsByStatistic() {
        MetricQueryRequest request = new MetricQueryRequest();
        request.setSensorIds(List.of(sensor.getId()));
        request.setMetrics(List.of(MetricType.TEMPERATURE, MetricType.HUMIDITY));
        request.setStatistic(StatisticType.AVG);
        request.setStartDate(LocalDate.now().minusDays(1));
        request.setEndDate(LocalDate.now());

        Map<Long, Map<String, Double>> result = metricQueryService.queryMetrics(request);

        assertThat(result).containsKey(sensor.getId());
        assertThat(result.get(sensor.getId()))
                .containsEntry("TEMPERATURE", (20.5 + 25.5) / 2)
                .containsEntry("HUMIDITY", 60.0);
    }
}
