package com.github.helendigger.kafkametricsproduce.metrics;

import com.github.helendigger.kafkametricsproduce.configuration.kafka.registry.KafkaRegistryConfig;
import com.github.helendigger.kafkametricsproduce.dto.Metric;
import com.github.helendigger.kafkametricsproduce.dto.MetricType;
import com.github.helendigger.kafkametricsproduce.infra.ServiceInfoProvider;
import com.github.helendigger.kafkametricsproduce.kafka.metrics.MetricsSender;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Kafka meter registry based on StepMeterRegistry
 * Only supports Counter and Gauge as an example
 */
@Component
public class KafkaRegistry extends StepMeterRegistry {
    private ServiceInfoProvider serviceInfoProvider;
    private MetricsSender metricsSender;

    @Autowired
    public KafkaRegistry(ServiceInfoProvider serviceInfoProvider,
                         MetricsSender metricsSender,
                         @Qualifier("kafka-registry-clock") Clock clock,
                         KafkaRegistryConfig config) {
        super(config, clock);
        this.serviceInfoProvider = serviceInfoProvider;
        this.metricsSender = metricsSender;
        start(new NamedThreadFactory("kafka-pusher"));
    }

    @PreDestroy
    public void stopRegistry() {
        this.stop();
    }

    private KafkaRegistry(StepRegistryConfig config, Clock clock) {
        super(config, clock);
    }

    @Override
    protected void publish() {
        Instant now = Instant.now();
        getMeters().stream().filter(meter -> isIn(meter,
                        List.of(Meter.Type.GAUGE, Meter.Type.COUNTER)))
                .sorted((m1, m2) -> {
                    int byType = compareByType(m1, m2);
                    if (byType == 0) {
                        return compareByName(m1, m2);
                    }
                    return byType;
                })
                .forEach(meter -> meter.use(x -> consumeGauge(x, now),
                        x -> consumeCounter(x, now),
                        (x)->{},
                        (x)->{},
                        (x)->{},
                        (x)->{},
                        (x)->{},
                        (x)->{},
                        (x)->{}));
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    private static boolean isIn(Meter meter, List<Meter.Type> types) {
        return types.contains(meter.getId().getType());
    }

    private void consumeGauge(Gauge meter, Instant now) {
        var convertedGauge = Metric
                .builder().name(meter.getId().getName())
                .value(meter.value())
                .type(MetricType.GAUGE)
                .timestamp(now).build();
        metricsSender.send(convertedGauge);
    }

    private void consumeCounter(Counter meter, Instant now) {
        var convertedCounter = Metric
                .builder().name(meter.getId().getName())
                .type(MetricType.COUNTER)
                .value(meter.count())
                .timestamp(now).build();
        metricsSender.send(convertedCounter);
    }

    private static int compareByType(Meter first, Meter second) {
        return first.getId().getType().compareTo(second.getId().getType());
    }

    private static int compareByName(Meter first, Meter second) {
        return first.getId().getName().compareTo(second.getId().getName());
    }
}
