package org.example.weatherdataingester.metric.service;

import org.example.common.model.MetricType;
import org.example.common.model.StatisticType;
import org.example.weatherdataingester.exception.InvalidDateRangeException;
import org.example.weatherdataingester.metric.dto.MetricQueryRequest;
import org.example.weatherdataingester.metric.repository.MetricValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")   // this is key
class MetricQueryServiceTest {

    @Mock
    private MetricValueRepository metricValueRepository;

    @InjectMocks
    private MetricQueryService metricQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private MetricQueryRequest buildRequest() {
        MetricQueryRequest req = new MetricQueryRequest();
        req.setStartDate(LocalDate.now().minusDays(2));
        req.setEndDate(LocalDate.now());
        req.setSensorIds(List.of(1L));
        req.setMetrics(List.of(MetricType.TEMPERATURE, MetricType.HUMIDITY));
        req.setStatistic(StatisticType.AVG);
        return req;
    }

    @Test
    void whenEndDateBeforeStart_shouldThrow() {
        MetricQueryRequest req = buildRequest();
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> metricQueryService.queryMetrics(req))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessageContaining("End date cannot be before start date");
    }

    @Test
    void whenRangeGreaterThan31Days_shouldThrow() {
        MetricQueryRequest req = buildRequest();
        req.setStartDate(LocalDate.now().minusDays(40));
        req.setEndDate(LocalDate.now());

        assertThatThrownBy(() -> metricQueryService.queryMetrics(req))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessageContaining("Date range must be between 1 day and 1 month");
    }

    @Test
    void whenValidRequest_shouldReturnNestedMap() {
        MetricQueryRequest req = buildRequest();


        Object[] row1 = new Object[]{1L, MetricType.TEMPERATURE, 22.5};
        Object[] row2 = new Object[]{1L, MetricType.HUMIDITY, 65.0};
        when(metricValueRepository.aggregateMetrics(any(), any(), any(), any(), any()))
                .thenReturn(List.of(row1, row2));

        Map<Long, Map<String, Double>> result = metricQueryService.queryMetrics(req);

        assertThat(result).containsKey(1L);
        assertThat(result.get(1L)).containsEntry("TEMPERATURE", 22.5);
        assertThat(result.get(1L)).containsEntry("HUMIDITY", 65.0);
    }

    @Test
    void whenSensorIdsNull_shouldPassNullToRepository() {
        MetricQueryRequest req = buildRequest();
        req.setSensorIds(null);

        when(metricValueRepository.aggregateMetrics(isNull(), any(), any(), any(), any()))
                .thenReturn(List.of());

        metricQueryService.queryMetrics(req);

        verify(metricValueRepository).aggregateMetrics(
                isNull(),
                anyList(),
                eq(StatisticType.AVG),
                any(LocalDate.class),
                any(LocalDate.class)
        );
    }
}