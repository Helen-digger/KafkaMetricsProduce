package com.github.helendigger.kafkametricsproduce.kafka.metrics;

import com.github.helendigger.kafkametricsproduce.configuration.kafka.KafkaProperties;
import com.github.helendigger.kafkametricsproduce.dto.Metric;
import com.github.helendigger.kafkametricsproduce.infra.ServiceInfoProvider;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service to send metrics to a Kafka metrics topic
 */
@Service
@AllArgsConstructor
public class MetricsSender {

    private KafkaProperties kafkaProperties;

    private ServiceInfoProvider serviceInfoProvider;

    private KafkaTemplate<String, Metric> kafkaTemplate;

    public void send(String serviceName, Metric metric) {
        kafkaTemplate.send(kafkaProperties.getMetricsTopicName(),
                serviceName, metric);
    }
    public void send(Metric metric) {
        send(serviceInfoProvider.getServiceName(), metric);
    }
}
