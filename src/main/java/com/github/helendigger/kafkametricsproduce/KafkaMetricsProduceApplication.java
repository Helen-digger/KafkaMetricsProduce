package com.github.helendigger.kafkametricsproduce;

import com.github.helendigger.kafkametricsproduce.configuration.kafka.KafkaProperties;
import com.github.helendigger.kafkametricsproduce.configuration.kafka.registry.KafkaRegistryConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableConfigurationProperties({KafkaProperties.class, KafkaRegistryConfig.class})
public class KafkaMetricsProduceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaMetricsProduceApplication.class, args);
    }

}
