package ru.lember.TelegrammerClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

@Slf4j
public class StompSessionHandler extends StompSessionHandlerAdapter {

    private static final String TOPIC_DESTINATION = "/topic/messages";

    private StompSession session = null;

    @Override
    public void afterConnected(StompSession session, StompHeaders stompHeaders) {
        log.info("Subscribed : " + session);

        session.subscribe(TOPIC_DESTINATION, this);

        this.session = session;
    }


    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return Request.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object payload) {
        Request request = (Request) payload;
        log.info("Received cmd: " + request.getCmd() + " id : " + request.getId());

        // todo add some logic
        Response response = new Response(request.getId(), request.getCmd(), "Test reply message");
        session.send(TOPIC_DESTINATION + "/reply", response);
    }

    @Override
    public void handleException(StompSession session, @Nullable StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {

        log.error("StompSessionHandler error: ", exception);
    }
}
