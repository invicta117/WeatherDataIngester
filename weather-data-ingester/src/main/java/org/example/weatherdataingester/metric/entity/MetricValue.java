package org.example.weatherdataingester.metric.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.model.MetricType;
import org.example.weatherdataingester.sensor.entity.Sensor;

import java.time.Instant;

@Entity
@Table(name = "metric_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricValue {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @Enumerated(EnumType.STRING)
    private MetricType type;

    private Double value;

    private Instant timestamp;
}