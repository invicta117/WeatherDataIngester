package org.example.common.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MetricType {
    TEMPERATURE("TEMPERATURE"),
    HUMIDITY("HUMIDITY"),
    WIND_SPEED("WIND_SPEED"),
    PRESSURE("PRESSURE");

    private final String label;

    MetricType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
