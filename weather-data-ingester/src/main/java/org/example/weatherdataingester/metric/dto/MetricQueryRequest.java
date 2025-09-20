package org.example.weatherdataingester.metric.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.common.model.MetricType;
import org.example.common.model.StatisticType;

import java.time.LocalDate;
import java.util.List;

@Data
public class MetricQueryRequest {

    private List<Long> sensorIds;

    @NotEmpty(message = "metrics cannot be null or empty")
    private List<@NotNull(message = "Metric type cannot be null") MetricType> metrics;

    private StatisticType statistic = StatisticType.AVG;

    private LocalDate startDate = LocalDate.now();

    private LocalDate endDate = LocalDate.now();
}