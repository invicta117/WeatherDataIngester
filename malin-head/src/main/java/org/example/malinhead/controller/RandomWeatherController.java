package org.example.malinhead.controller;

import org.example.common.model.MetricType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class RandomWeatherController {

    @GetMapping("/api/malin-head")
    public Map<MetricType, Double> getRandomWeatherData() {
        Map<MetricType, Double> data = new EnumMap<>(MetricType.class);
        data.put(MetricType.TEMPERATURE, randomDouble(-10, 40));
        data.put(MetricType.HUMIDITY, randomDouble(20, 100));
        data.put(MetricType.WIND_SPEED, randomDouble(0, 25));
        data.put(MetricType.PRESSURE, randomDouble(950, 1050));
        return data;
    }

    private double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}
