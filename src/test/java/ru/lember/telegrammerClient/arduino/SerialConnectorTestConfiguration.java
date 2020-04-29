package ru.lember.telegrammerClient.arduino;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.lember.telegrammerClient.config.ArduinoUpdateProcessorImpl;
import ru.lember.telegrammerClient.config.SerialProperties;

@Configuration
public class SerialConnectorTestConfiguration {

    @Bean
    Connector connector() {
        SerialProperties properties = Mockito.mock(SerialProperties.class);
        Mockito.when(properties.getCmdBeginMarker()).thenReturn('^');
        Mockito.when(properties.getCmdEndMarker()).thenReturn('$');
        Mockito.when(properties.getCmdSeparator()).thenReturn('#');


        return new SerialConnector(properties, new ArduinoUpdateProcessorImpl(), true);
    }

}
