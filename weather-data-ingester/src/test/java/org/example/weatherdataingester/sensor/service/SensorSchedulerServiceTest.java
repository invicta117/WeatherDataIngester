package org.example.weatherdataingester.sensor.service;

import org.example.weatherdataingester.metric.dto.MetricBatchRequest;
import org.example.weatherdataingester.metric.service.WeatherDataService;
import org.example.weatherdataingester.sensor.config.SensorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class SensorSchedulerServiceTest {

    @Mock
    private WeatherDataService weatherDataService;

    @Mock
    private SensorFetchService sensorFetchService;

    @Mock
    private SensorConfig sensorConfig;

    @InjectMocks
    private SensorSchedulerService schedulerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenValidDataFetched_shouldSaveMetrics() {
        SensorConfig.SensorSource sensor = new SensorConfig.SensorSource();
        sensor.setId(1L);
        sensor.setUrl("http://sensor.test");

        when(sensorConfig.getSensors()).thenReturn(List.of(sensor));
        when(sensorFetchService.fetchSensorData(sensor.getUrl()))
                .thenReturn(Map.of("temperature", 20.5, "humidity", 60));

        schedulerService.fetchWeatherData();

        verify(weatherDataService, times(1)).saveMetrics(any(MetricBatchRequest.class));
    }

    @Test
    void whenEmptyDataFetched_shouldNotSaveMetrics() {
        SensorConfig.SensorSource sensor = new SensorConfig.SensorSource();
        sensor.setId(2L);
        sensor.setUrl("http://sensor.empty");

        when(sensorConfig.getSensors()).thenReturn(List.of(sensor));
        when(sensorFetchService.fetchSensorData(sensor.getUrl()))
                .thenReturn(Map.of());

        schedulerService.fetchWeatherData();

        verify(weatherDataService, never()).saveMetrics(any());
    }

    @Test
    void whenInvalidMetricKey_shouldIgnoreIt() {
        SensorConfig.SensorSource sensor = new SensorConfig.SensorSource();
        sensor.setId(3L);
        sensor.setUrl("http://sensor.invalid");

        when(sensorConfig.getSensors()).thenReturn(List.of(sensor));
        when(sensorFetchService.fetchSensorData(sensor.getUrl()))
                .thenReturn(Map.of("foo", 999));

        schedulerService.fetchWeatherData();

        ArgumentCaptor<MetricBatchRequest> captor = ArgumentCaptor.forClass(MetricBatchRequest.class);
        verify(weatherDataService).saveMetrics(captor.capture());

        MetricBatchRequest savedBatch = captor.getValue();
        assert savedBatch.getMetrics().isEmpty();
    }
}