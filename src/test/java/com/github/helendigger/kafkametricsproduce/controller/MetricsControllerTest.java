package com.github.helendigger.kafkametricsproduce.controller;

import com.github.helendigger.kafkametricsproduce.controller.configuration.MetricsControllerConfiguration;
import com.github.helendigger.kafkametricsproduce.dto.Metric;
import com.github.helendigger.kafkametricsproduce.dto.MetricType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@SpringBootTest(classes = MetricsControllerConfiguration.class)
public class MetricsControllerTest {
    @Autowired
    private MetricsController metricsController;

    @Test
    public void testMetricsPost() {
        var metric = Metric.builder()
                .value(2.0)
                .name("sample")
                .serviceName("service")
                .type(MetricType.COUNTER)
                .timestamp(Instant.now()).build();
        Assertions.assertDoesNotThrow(() -> {
            var answer = metricsController.postMetric(metric);
            Assertions.assertEquals(HttpStatus.ACCEPTED, answer.getStatusCode());
        });
    }
}
