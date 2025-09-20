package org.example.weatherdataingester.metric.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.model.MetricType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricData {
    @NotNull
    private MetricType type;
    @NotNull
    private Double value;
}