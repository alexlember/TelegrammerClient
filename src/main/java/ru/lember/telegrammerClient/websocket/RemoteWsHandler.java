package ru.lember.telegrammerClient.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.*;
import reactor.core.publisher.ReplayProcessor;
import ru.lember.telegrammerClient.arduino.Connector;
import ru.lember.telegrammerClient.dto.in.RequestFromRemote;
import ru.lember.telegrammerClient.dto.in.ResponseToRemote;
import ru.lember.telegrammerClient.dto.inner.Direction;
import ru.lember.telegrammerClient.dto.out.RequestFromRpi;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.time.Duration;

@Slf4j
public class RemoteWsHandler extends StompSessionHandlerAdapter {

    private static final String TOPIC_DESTINATION = "/topic/messages";
    private static final String TOPIC_REQUEST_DESTINATION = "/topic/requests";

    private StompSession session = null;

    private final Connector connector;
    private final ReplayProcessor<WsEvent> processor;

    @Autowired
    public RemoteWsHandler(ReplayProcessor<WsEvent> processor, Connector connector) {
        this.processor = processor;
        this.connector = connector;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("initialized");
    }

    @PostConstruct
    public void preDestroy() {
        log.info("destroying");
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders stompHeaders) {
        processor.onNext(new WsEvent(EventType.CONNECTION_ESTABLISHED, session));

        session.subscribe(TOPIC_DESTINATION, this);
        log.info("Subscribed on incoming requests from remote for session id: {}", session.getSessionId());

        this.session = session;

        connector.processor().filter(req -> Direction.REQUEST == req.getDirection())
                .subscribe(req -> {
                    log.info("Received request from Arduino: {}. Sending request to the remote server", req);
                    RequestFromRpi requestFromRpi = new RequestFromRpi(req.getCmd(), req.getMessage());
                    session.send(TOPIC_REQUEST_DESTINATION, requestFromRpi);
                }, error -> log.error("Error on ArduinoDataUpdate: ", error));
    }


    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return RequestFromRemote.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object payload) {
        RequestFromRemote requestFromRemote = (RequestFromRemote) payload;

        log.info("Received cmd from remote: " + requestFromRemote);

        connector.processor().filter(req -> Direction.RESPONSE == req.getDirection() && requestFromRemote.getId().equals(req.getId()))
                .timeout(Duration.ofMillis(requestFromRemote.getTimeoutMs()))
                .take(1)
                .subscribe(req -> {
                    log.info("Received response from Arduino: {}. Sending response to the remote server", req);
                    ResponseToRemote responseToRemote = new ResponseToRemote(req.getId(), req.getCmd(), req.getMessage());
                    session.send(TOPIC_DESTINATION + "/reply", responseToRemote);
                }, error -> log.error("Error on receiving ArduinoDataUpdate response: {}", error.toString()));

        connector.send(requestFromRemote);
    }

    @Override
    public void handleException(StompSession session, @Nullable StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {

        log.error("RemoteWsHandler error: {}", exception.toString());
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {

        if (exception instanceof ConnectionLostException) {
            processor.onNext(new WsEvent(EventType.CONNECTION_LOST, session));
        }

    }
}
