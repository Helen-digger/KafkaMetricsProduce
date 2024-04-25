package com.github.helendigger.kafkametricsproduce.configuration.kafka.registry;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@ConfigurationProperties(prefix = "kafka.registry.config")
@Getter
@Setter
public class KafkaRegistryConfig implements StepRegistryConfig {

    private Map<String, String> properties;

    @Override
    public String prefix() {
        return "kafka";
    }

    @Override
    public String get(String key) {
        return properties.get(key);
    }

    @Bean(name = "kafka-registry-clock")
    public Clock getKafkaDefaultClock() {
        return Clock.SYSTEM;
    }
}
