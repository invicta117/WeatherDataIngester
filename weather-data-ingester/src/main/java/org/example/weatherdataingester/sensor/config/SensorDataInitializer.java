package org.example.weatherdataingester.sensor.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.weatherdataingester.sensor.entity.Sensor;
import org.example.weatherdataingester.sensor.repository.SensorRepository;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class SensorDataInitializer {

    private final SensorConfig sensorConfig;
    private final SensorRepository sensorRepository;

    @PostConstruct
    public void initSensors() {
        sensorConfig.getSensors().forEach(source -> {
            if (sensorRepository.existsById(source.getId())) {
                log.debug("Sensor {} ({}) already exists, skipping", source.getName(), source.getId());
                return;
            }

            Sensor sensor = new Sensor();
            sensor.setId(source.getId());
            sensor.setName(source.getName());
            sensorRepository.saveAndFlush(sensor);

            log.info("Inserted sensor {} ({}) into DB", source.getName(), source.getId());
        });
    }
}
