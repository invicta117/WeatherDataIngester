package org.example.weatherdataingester.metric.service;

import org.example.weatherdataingester.metric.dto.MetricBatchRequest;
import org.example.weatherdataingester.metric.dto.MetricData;
import org.example.weatherdataingester.metric.entity.MetricValue;
import org.example.weatherdataingester.sensor.entity.Sensor;
import org.example.weatherdataingester.sensor.repository.SensorRepository;
import org.example.weatherdataingester.metric.repository.MetricValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")   // this is key
class WeatherDataServiceIT {

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
    private WeatherDataService weatherDataService;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private MetricValueRepository metricValueRepository;

    @BeforeEach
    void setUp() {
        metricValueRepository.deleteAll();
        sensorRepository.deleteAll();

        sensorRepository.save(new Sensor(1L, "Test Sensor"));
    }

    @Test
    void testSaveMetrics_savesDataCorrectly() {
        // given
        MetricBatchRequest request = new MetricBatchRequest();
        request.setSensorId(1L);
        request.setTimestamp(Instant.now());

        MetricData metric = new MetricData();
        metric.setType(org.example.common.model.MetricType.TEMPERATURE);
        metric.setValue(22.5);

        request.setMetrics(List.of(metric));

        // when
        weatherDataService.saveMetrics(request);

        // then
        List<MetricValue> saved = metricValueRepository.findAll();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getSensor().getId()).isEqualTo(1L);
        assertThat(saved.get(0).getType()).isEqualTo(org.example.common.model.MetricType.TEMPERATURE);
        assertThat(saved.get(0).getValue()).isEqualTo(22.5);
    }
}