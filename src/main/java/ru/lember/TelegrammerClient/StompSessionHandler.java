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

    private StompSession session = null;

    @Override
    public void afterConnected(StompSession session, StompHeaders stompHeaders) {
        this.session = session;

        session.subscribe("/topic/messages", this);
    }

    @Override
    public void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.error(exception.toString());
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.error(exception.toString());
    }


    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return Request.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object payload) {
        Request request = (Request) payload;
        log.info("Received : " + request.getCmd() + " id : " + request.getId());

        Response response = new Response(request.getId(), request.getCmd(), "Success");
        session.send("/app/cmd", response);
    }
}
