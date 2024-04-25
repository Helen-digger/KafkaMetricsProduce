package com.github.helendigger.kafkametricsproduce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Class that represents some application metric
 */
@Data
@Builder
public class Metric {
    @NotNull
    private String name;
    @NotNull
    private MetricType type;
    @NotNull
    private Double value;
    @NotNull
    private Instant timestamp;
    @NotNull
    private String serviceName;
}
