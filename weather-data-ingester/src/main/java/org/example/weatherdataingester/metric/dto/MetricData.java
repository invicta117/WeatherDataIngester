package org.example.weatherdataingester.metric.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.common.model.MetricType;

@Data
public class MetricData {
    @NotNull
    private MetricType type;
    @NotNull
    private Double value;
}