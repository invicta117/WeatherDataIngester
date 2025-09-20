package org.example.weatherdataingester.metric.repository;

import org.example.weatherdataingester.metric.entity.MetricValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricValueRepository extends JpaRepository<MetricValue, Long>, MetricValueRepositoryCustom {
}
