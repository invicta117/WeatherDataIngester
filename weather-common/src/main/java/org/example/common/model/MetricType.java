package org.example.common.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MetricType {
    TEMPERATURE("temperature"),
    HUMIDITY("humidity"),
    WIND_SPEED("wind_speed"),
    PRESSURE("pressure");

    private final String label;

    MetricType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
