package org.example.weatherdataingester.metric.service;

import org.example.common.model.MetricType;
import org.example.weatherdataingester.exception.InvalidSensorException;
import org.example.weatherdataingester.metric.dto.MetricBatchRequest;
import org.example.weatherdataingester.metric.dto.MetricData;
import org.example.weatherdataingester.metric.entity.MetricValue;
import org.example.weatherdataingester.metric.repository.MetricValueRepository;
import org.example.weatherdataingester.sensor.entity.Sensor;
import org.example.weatherdataingester.sensor.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class WeatherDataServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private MetricValueRepository metricValueRepository;

    @InjectMocks
    private WeatherDataService weatherDataService;

    private MetricBatchRequest buildBatchRequest(Long sensorId) {
        MetricData temp = new MetricData(MetricType.TEMPERATURE, 22.5);
        MetricData humidity = new MetricData(MetricType.HUMIDITY, 65.0);

        MetricBatchRequest req = new MetricBatchRequest();
        req.setSensorId(sensorId);
        req.setTimestamp(Instant.now());
        req.setMetrics(List.of(temp, humidity));
        return req;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenSensorNotFound_shouldThrowInvalidSensorException() {
        when(sensorRepository.findById(99L)).thenReturn(Optional.empty());

        MetricBatchRequest req = buildBatchRequest(99L);

        assertThatThrownBy(() -> weatherDataService.saveMetrics(req))
                .isInstanceOf(InvalidSensorException.class)
                .hasMessageContaining("Sensor id: 99 not found");
    }

    @Test
    void whenSensorExists_shouldSaveEachMetricValue() {
        Sensor sensor = new Sensor(1L, "shirkin");
        when(sensorRepository.findById(1L)).thenReturn(Optional.of(sensor));

        MetricBatchRequest req = buildBatchRequest(1L);

        weatherDataService.saveMetrics(req);


        verify(metricValueRepository, times(2)).save(any(MetricValue.class));
    }

    @Test
    void whenSavingMetricValues_shouldLinkCorrectSensor() {
        Sensor sensor = new Sensor(1L, "shirkin");
        when(sensorRepository.findById(1L)).thenReturn(Optional.of(sensor));

        MetricBatchRequest req = buildBatchRequest(1L);

        weatherDataService.saveMetrics(req);

        ArgumentCaptor<MetricValue> captor = ArgumentCaptor.forClass(MetricValue.class);
        verify(metricValueRepository, times(2)).save(captor.capture());

        List<MetricValue> savedMetrics = captor.getAllValues();
        assertThat(savedMetrics).allMatch(mv -> mv.getSensor().equals(sensor));
    }
}
