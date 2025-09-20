package org.example.weatherdataingester.metric.repository;

import org.example.common.model.StatisticType;

import java.time.LocalDate;
import java.util.List;

public interface MetricValueRepositoryCustom {
    List<Object[]> aggregateMetrics(
            List<Long> sensorIds,
            List<String> metrics,
            StatisticType statistic,
            LocalDate start,
            LocalDate end
    );
}