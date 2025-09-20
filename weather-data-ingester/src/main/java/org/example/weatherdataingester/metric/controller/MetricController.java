package org.example.weatherdataingester.metric.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.weatherdataingester.metric.dto.MetricQueryRequest;
import org.example.weatherdataingester.metric.service.MetricQueryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/metrics/query")
@Tag(name = "Metrics API", description = "Query weather sensor data")
@RequiredArgsConstructor
public class MetricController {

    private final MetricQueryService metricQueryService;

    @PostMapping
    @Operation(summary = "Receive new metric values", description = "Accepts weather metrics (temperature, humidity, wind speed, etc.) from sensors")
    public Map<Long, Map<String, Double>> receiveMetrics(@Valid @RequestBody MetricQueryRequest request) {
        return metricQueryService.queryMetrics(request);
    }
}
