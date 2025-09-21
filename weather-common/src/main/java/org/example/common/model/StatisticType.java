package org.example.common.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatisticType {
    MIN("MIN"),
    MAX("MAX"),
    SUM("SUM"),
    AVG("AVG");

    private final String label;

    StatisticType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

}