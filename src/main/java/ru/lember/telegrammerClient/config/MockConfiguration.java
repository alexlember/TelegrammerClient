package ru.lember.telegrammerClient.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.lember.telegrammerClient.arduino.Connector;
import ru.lember.telegrammerClient.arduino.MockConnector;

import javax.annotation.PostConstruct;

@Profile("mock")
@Slf4j
@Configuration
public class MockConfiguration {

    @Value("${application.mockRequestSendingPeriodSec:30}")
    private Long mockRequestSendingPeriodSec;

    @Value("${application.mockCmdExecutionDelayMs}")
    private Long mockCmdExecutionDelayMs;

    @PostConstruct
    private void postConstruct() {
        log.info("initialized");
    }

    @Bean
    public Connector connector(final SerialProperties serialProperties) {
        return new MockConnector(serialProperties, mockRequestSendingPeriodSec, mockCmdExecutionDelayMs);
    }

}
