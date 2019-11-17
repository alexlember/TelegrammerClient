package ru.lember.telegrammerClient.websocket;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import reactor.core.publisher.ReplayProcessor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConnectionControllerImpl implements ConnectionController {

    private final String wsConnectUrl;
    private final StompSessionHandlerAdapter webSocketHandler;
    private final WebSocketStompClient webSocketStompClient;
    private final ReplayProcessor<WsEvent> processor;

    private long connectionTimeoutMs;
    private long tryReconnectEveryMs;

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private @Nullable ScheduledFuture<?> futureReconnection = null;

    public ConnectionControllerImpl(final String wsConnectUrl,
                                    final StompSessionHandlerAdapter webSocketHandler,
                                    final WebSocketStompClient webSocketStompClient,
                                    final ReplayProcessor<WsEvent> processor,
                                    final long connectionTimeoutMs,
                                    final long tryReconnectEveryMs) {
        this.wsConnectUrl = wsConnectUrl;
        this.webSocketHandler = webSocketHandler;
        this.webSocketStompClient = webSocketStompClient;
        this.processor = processor;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.tryReconnectEveryMs = tryReconnectEveryMs;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("initialized");
        connect();
    }

    @PreDestroy
    public void preDestroy() {
        executorService.shutdown();
        log.info("destroying");
    }

    @Override
    public void connect() {
        log.info("Ws connecting url: {}", wsConnectUrl);

        processor
                .filter(wsEvent -> wsEvent.getEventType() == EventType.CONNECTION_ESTABLISHED)
                .take(1)
                .timeout(Duration.ofMillis(connectionTimeoutMs))
                .subscribe(
                        wsEvent -> {

                            String sessionId = wsEvent.getSession() != null ? wsEvent.getSession().getSessionId() : "?";
                            log.info("WS connection established. Session id: {}", sessionId);

                            if (futureReconnection != null) {
                                futureReconnection.cancel(true);
                            }
                            subscribeOnTransportError(sessionId);
                        },
                        err -> {
                            log.error("Can't connect to the ws server: {}", err.toString());

                            if (futureReconnection == null || futureReconnection.isDone()) {
                                log.info("Scheduling new reconnection attempt in {} ms", tryReconnectEveryMs);
                                futureReconnection = executorService.schedule(this::connect, tryReconnectEveryMs, TimeUnit.MILLISECONDS);
                            }
                        });

        webSocketStompClient.connect(wsConnectUrl, webSocketHandler);
    }


    private void subscribeOnTransportError(@NotNull String sessionId) {

        log.info("WS subscribing on transport error for session id: {}", sessionId);

        processor
                .filter(wsEvent -> wsEvent.getEventType() == EventType.CONNECTION_LOST)
                .take(1)
                .subscribe(
                        __ -> {
                            log.warn("WS connection lost detected. Reconnection...");
                            connect();
                        },
                        err -> log.error("Can't handle ws transport error: {}", err.toString()));
    }

}
