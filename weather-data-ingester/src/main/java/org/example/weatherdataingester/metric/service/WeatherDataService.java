package org.example.weatherdataingester.metric.service;

import lombok.RequiredArgsConstructor;
import org.example.weatherdataingester.metric.dto.MetricBatchRequest;
import org.example.weatherdataingester.metric.dto.MetricData;
import org.example.weatherdataingester.metric.entity.MetricValue;
import org.example.weatherdataingester.sensor.entity.Sensor;
import org.example.weatherdataingester.exception.InvalidSensorException;
import org.example.weatherdataingester.metric.repository.MetricValueRepository;
import org.example.weatherdataingester.sensor.repository.SensorRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherDataService {

    private final SensorRepository sensorRepository;
    private final MetricValueRepository metricValueRepository;

    public void saveMetrics(MetricBatchRequest request) {
        Sensor sensor = sensorRepository.findById(request.getSensorId()).orElseThrow(() -> new InvalidSensorException("Sensor id: " + request.getSensorId() + " not found"));

        for (MetricData metric : request.getMetrics()) {
            MetricValue mv = new MetricValue();
            mv.setSensor(sensor);
            mv.setType(metric.getType());
            mv.setValue(metric.getValue());
            mv.setTimestamp(request.getTimestamp());
            metricValueRepository.save(mv);
        }
    }
}