package org.example.weatherdataingester.sensor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.model.MetricType;
import org.example.weatherdataingester.metric.dto.MetricBatchRequest;
import org.example.weatherdataingester.metric.dto.MetricData;
import org.example.weatherdataingester.metric.service.WeatherDataService;
import org.example.weatherdataingester.sensor.config.SensorConfig;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorSchedulerService {
    private static final Set<String> VALID_METRICS = Arrays.stream(MetricType.values())
            .map(mt -> mt.name().toLowerCase())
            .collect(Collectors.toUnmodifiableSet());
    private final WeatherDataService weatherDataService;
    private final SensorConfig sensorConfig;
    private final SensorFetchService sensorFetchService;

    @Scheduled(fixedRate = 10000)
    public void fetchWeatherData() {

        for (SensorConfig.SensorSource sensor : sensorConfig.getSensors()) {
            try {
                Map<String, Object> response = sensorFetchService.fetchSensorData(sensor.getUrl());
                if (response.isEmpty()) {
                    log.warn("No data found for sensor {}", sensor.getUrl());
                    continue;
                }
                MetricBatchRequest batch = mapToMetricBatchRequest(sensor.getId(), response);
                weatherDataService.saveMetrics(batch);
                log.info("Fetched weather data: {}", response);
            } catch (Exception e) {
                log.error("Failed to fetch data from simulator", e);
            }
        }
    }


    private MetricBatchRequest mapToMetricBatchRequest(Long sensorId, Map<String, Object> response) {
        MetricBatchRequest batch = new MetricBatchRequest();
        batch.setSensorId(sensorId);
        batch.setTimestamp(Instant.now());

        List<MetricData> metrics = new ArrayList<>();
        for (Map.Entry<String, Object> entry : response.entrySet()) {
            MetricData md = new MetricData();
            if (VALID_METRICS.contains(entry.getKey().toLowerCase())) {
                md.setType(MetricType.valueOf(entry.getKey().toUpperCase()));
                md.setValue(Double.valueOf(String.valueOf(entry.getValue())));
                metrics.add(md);
            }
        }
        batch.setMetrics(metrics);
        return batch;
    }
}
