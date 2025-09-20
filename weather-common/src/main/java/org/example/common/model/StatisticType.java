package org.example.common.model;

import com.fasterxml.jackson.annotation.JsonValue;

@Schema(enumAsRef = true, description = "Supported metric types")
public enum StatisticType {
    MIN("min"),
    MAX("max"),
    SUM("sum"),
    AVG("avg");

    private final String label;

    StatisticType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

}