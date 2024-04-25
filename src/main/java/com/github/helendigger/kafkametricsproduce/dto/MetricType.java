package com.github.helendigger.kafkametricsproduce.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

/**
 * Enum representing available metric types
 */
public enum MetricType {
    COUNTER,
    GAUGE;
    @JsonCreator
    public static MetricType forName(String name) {
        return Arrays.stream(values()).filter(m -> m.name().equals(name))
                .findFirst().orElseThrow();
    }
}
