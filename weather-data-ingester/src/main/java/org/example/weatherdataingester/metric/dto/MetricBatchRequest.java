package org.example.weatherdataingester.metric.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class MetricBatchRequest {
    @NotNull
    private Long sensorId;

    @NotNull
    private Instant timestamp;

    @NotNull
    private List<MetricData> metrics;
}