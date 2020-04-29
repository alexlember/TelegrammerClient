package ru.lember.telegrammerClient.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.lember.telegrammerClient.arduino.Connector;
import ru.lember.telegrammerClient.arduino.SerialConnector;

import javax.annotation.PostConstruct;

@Profile("real")
@Slf4j
@Configuration
public class RealConfiguration {

    @Value("${application.isInTestMode:false}")
    private Boolean isInTestMode;

    @PostConstruct
    private void postConstruct() {
        log.info("initialized");
    }

    @Bean
    public Connector connector(final SerialProperties serialProperties) {
        return new SerialConnector(serialProperties, new ArduinoUpdateProcessorImpl(), isInTestMode);
    }

}
