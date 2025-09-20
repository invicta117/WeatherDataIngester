package org.example.weatherdataingester.metric.service;

import lombok.RequiredArgsConstructor;
import org.example.common.model.MetricType;
import org.example.weatherdataingester.exception.InvalidDateRangeException;
import org.example.weatherdataingester.metric.dto.MetricQueryRequest;
import org.example.weatherdataingester.metric.repository.MetricValueRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MetricQueryService {

    private final MetricValueRepository metricValueRepository;

    public Map<Long, Map<String, Double>> queryMetrics(MetricQueryRequest req) {
        LocalDate start = req.getStartDate();
        LocalDate end = req.getEndDate();

        if (end.isBefore(start)) {
            throw new InvalidDateRangeException("End date cannot be before start date");
        }

        long daysBetween = ChronoUnit.DAYS.between(start, end);
        if (daysBetween < 0 || daysBetween > 31) {
            throw new InvalidDateRangeException("Date range must be between 1 day and 1 month");
        }

        List<Long> sensorIds = (req.getSensorIds() == null || req.getSensorIds().isEmpty()) ? null : req.getSensorIds();

        List<Object[]> rows = metricValueRepository.aggregateMetrics(
                sensorIds,
                req.getMetrics().stream().map(Enum::name).toList(),
                req.getStatistic(),
                start,
                end
        );

        Map<Long, Map<String, Double>> result = new HashMap<>();
        for (Object[] row : rows) {
            Long sensorId = (Long) row[0];
            if (!result.containsKey(sensorId)) {
                HashMap<String, Double> metricsSensor = new HashMap<>();
                result.put(sensorId, metricsSensor);
            }
            MetricType metric = (MetricType) row[1];
            Double value = (Double) row[2];
            result.get(sensorId).put(metric.name(), value);
        }
        return result;
    }
}