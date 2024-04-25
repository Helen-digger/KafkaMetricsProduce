package com.github.helendigger.kafkametricsproduce.configuration.kafka.topic;

import com.github.helendigger.kafkametricsproduce.configuration.kafka.KafkaProperties;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class KafkaTopicConfiguration {

    private KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic metricsTopic() {
        return TopicBuilder.name(kafkaProperties.getMetricsTopicName())
                .partitions(kafkaProperties.getPartitionCount())
                .replicas(kafkaProperties.getReplicasCount())
                .compact()
                .build();
    }
}
