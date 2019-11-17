package ru.lember.telegrammerClient.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import reactor.core.publisher.ReplayProcessor;
import ru.lember.telegrammerClient.arduino.Connector;
import ru.lember.telegrammerClient.websocket.ConnectionController;
import ru.lember.telegrammerClient.websocket.ConnectionControllerImpl;
import ru.lember.telegrammerClient.websocket.RemoteWsHandler;
import ru.lember.telegrammerClient.websocket.WsEvent;

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

    @Value("${websocket.connectionTimeoutMs:5000}")
    private long connectionTimeoutMs;

    @Value("${websocket.tryReconnectEveryMs:5000}")
    private long tryReconnectEveryMs;

    @PostConstruct
    public void postConstruct() {
        log.info("initialized. webSocketServerScheme: {}, webSocketServerHost: {}, webSocketServerUrl:{}, connectionTimeoutMs: {}, tryReconnectEveryMs: {}",
                webSocketServerScheme, webSocketServerHost, webSocketServerPort, webSocketServerUrl, connectionTimeoutMs, tryReconnectEveryMs);
    }

    @Bean
    public ReplayProcessor<WsEvent> processor() {
        return ReplayProcessor.create(1);
    }

    @Bean
    public ConnectionController connectionController(final RemoteWsHandler webSocketHandler,
                                                     final WebSocketStompClient webSocketStompClient,
                                                     final ReplayProcessor<WsEvent> processor) {
        String url = webSocketServerScheme
                + "://"
                + webSocketServerHost
                + ":"
                + webSocketServerPort
                + webSocketServerUrl;


        return new ConnectionControllerImpl(url, webSocketHandler, webSocketStompClient, processor, connectionTimeoutMs, tryReconnectEveryMs);
    }

    @Bean
    public WebSocketStompClient webSocketClient() {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        return stompClient;
    }

    @Bean
    public RemoteWsHandler webSocketHandler(final ReplayProcessor<WsEvent> processor, final Connector connector) {
        return new RemoteWsHandler(processor, connector);
    }

}
