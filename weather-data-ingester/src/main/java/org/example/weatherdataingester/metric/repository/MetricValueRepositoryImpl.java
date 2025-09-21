package org.example.weatherdataingester.metric.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.example.common.model.StatisticType;
import org.example.weatherdataingester.exception.InvalidSensorException;
import org.example.weatherdataingester.sensor.entity.Sensor;
import org.example.weatherdataingester.sensor.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class MetricValueRepositoryImpl implements MetricValueRepositoryCustom {

    public static final String SENSOR_IDS = "sensorIds";
    public static final String METRICS = "metrics";
    public static final String START = "start";
    public static final String END = "end";
    public static final String QUERY = """
            SELECT mv.sensor.id, mv.type, %s(mv.value)
            FROM MetricValue mv
            WHERE (mv.sensor.id IN :sensorIds)
              AND mv.type IN :metrics
              AND date(mv.timestamp) BETWEEN :start AND :end
            GROUP BY mv.sensor.id, mv.type
            """;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SensorRepository sensorRepository;

    @Override
    public List<Object[]> aggregateMetrics(List<Long> sensorIds,
                                           List<String> metrics,
                                           StatisticType statistic,
                                           LocalDate start,
                                           LocalDate end) {

        List<Long> sensors = sensorRepository.findAll().stream().map(Sensor::getId).toList();

        if (sensorIds == null || sensorIds.isEmpty()) {
            sensorIds = sensors;
        }

        for (Long sensorId : sensorIds) {
            if (!sensors.contains(sensorId)) {
                throw new InvalidSensorException("Sensor id " + sensorId + " does not exist");
            }
        }

        String jpql = QUERY.formatted(statistic.name().toLowerCase());

        Query query = entityManager.createQuery(jpql);
        query.setParameter(SENSOR_IDS, sensorIds);
        query.setParameter(METRICS, metrics);
        query.setParameter(START, start);
        query.setParameter(END, end);

        return query.getResultList();
    }
}