package com.github.helendigger.kafkametricsproduce.infra;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Component that provides information about service
 */
@Component
@NoArgsConstructor
@Slf4j
public class ServiceInfoProvider {

    @Value("${service.base.name:kafka-producer}")
    private String serviceBaseName;

    public String getServiceName() {
        return serviceBaseName + ":" + instanceName;
    }

    private static final String instanceName;

    static {
        instanceName = getFromEnvironment().orElseGet(() -> getFromNetwork().orElse("unknown"));
    }

    private static Optional<String> getFromEnvironment() {
        return Optional.ofNullable(System.getenv("HOSTNAME")).filter(Predicate.not(String::isBlank));
    }

    private static Optional<String> getFromNetwork() {
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            return Optional.of(hostName).filter(Predicate.not(String::isBlank));
        } catch (UnknownHostException e) {
            return Optional.empty();
        }
    }
}
