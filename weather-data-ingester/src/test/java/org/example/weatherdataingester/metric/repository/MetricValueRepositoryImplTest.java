package org.example.weatherdataingester.metric.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.example.common.model.StatisticType;
import org.example.weatherdataingester.exception.InvalidSensorException;
import org.example.weatherdataingester.metric.repository.MetricValueRepositoryImpl;
import org.example.weatherdataingester.sensor.entity.Sensor;
import org.example.weatherdataingester.sensor.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")   // this is key
class MetricValueRepositoryImplTest {

    @InjectMocks
    private MetricValueRepositoryImpl repository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(new Object[]{"dummy"}));
    }

    @Test
    void whenSensorIdsNull_shouldUseAllSensors() {
        when(sensorRepository.findAll()).thenReturn(List.of(
                new Sensor(1L, "shirkin"),
                new Sensor(2L, "malin")
        ));

        repository.aggregateMetrics(
                null,
                List.of("TEMPERATURE"),
                StatisticType.AVG,
                LocalDate.now().minusDays(1),
                LocalDate.now()
        );

        verify(query).setParameter("sensorIds", List.of(1L, 2L));
    }

    @Test
    void whenSensorIdInvalid_shouldThrowException() {
        when(sensorRepository.findAll()).thenReturn(List.of(new Sensor(1L, "shirkin")));

        assertThatThrownBy(() ->
                repository.aggregateMetrics(
                        List.of(99L),
                        List.of("TEMPERATURE"),
                        StatisticType.AVG,
                        LocalDate.now().minusDays(1),
                        LocalDate.now()
                )
        ).isInstanceOf(InvalidSensorException.class)
                .hasMessageContaining("Sensor id 99 does not exist");
    }
}