package org.example.weatherdataingester.sensor.repository;

import org.example.weatherdataingester.sensor.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
}