package ru.lember.TelegrammerClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class WebSocketConfiguration {

    @Value("${websocket.server.scheme}")
    private String webSocketServerScheme;

    @Value("${websocket.server.host}")
    private String webSocketServerHost;

    @Value("${websocket.server.port}")
    private Integer webSocketServerPort;

    @Value("${websocket.server.url}")
    private String webSocketServerUrl;

    @PostConstruct
    public void postConstruct() {
        log.info("initialized. webSocketServerScheme: {}, webSocketServerHost: {}, webSocketServerUrl:{}",
                webSocketServerScheme, webSocketServerHost, webSocketServerPort, webSocketServerUrl);
    }

    @Bean
    public WebSocketClient webSocketClient() {
        String url = webSocketServerScheme
                + "://"
                + webSocketServerHost
                + ":"
                + webSocketServerPort
                + webSocketServerUrl;

        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSessionHandler sessionHandler = new StompSessionHandler();
        stompClient.connect(url, sessionHandler);

        return client;
    }
}
