package ru.lember.TelegrammerClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.support.GenericMessage;

import java.lang.reflect.Type;

@Slf4j
public class StompSessionHandler extends StompSessionHandlerAdapter {

    private StompSession session = null;

//    @Override
//    public void afterConnected(StompSession session, StompHeaders stompHeaders) {
//        this.session = session;
//
//        session.subscribe("topic/greetings", this);
//
//        //session.subscribe("/user/queue/cmd", this);
//    }
//
//    @Override
//    public void handleException(StompSession session,
//                                @Nullable StompCommand command,
//                                StompHeaders headers,
//                                byte[] payload,
//                                Throwable exception) {
//        log.error(exception.toString());
//    }
//
//    @Override
//    public void handleTransportError(StompSession session, Throwable exception) {
//        log.error(exception.toString());
//    }
//
//
//    @Override
//    public Type getPayloadType(StompHeaders stompHeaders) {
//        return Request.class;
//    }
//
//    @Override
//    public void handleFrame(StompHeaders stompHeaders, Object payload) {
//        Request request = (Request) payload;
//        log.info("Received : " + request.getCmd() + " id : " + request.getId());
//
//        Response response = new Response(request.getId(), request.getCmd(), "Success");
//        //session.send("/app/cmd", response);
//        session.send("topic/greetings", response);
//    }

    @Override
    public void afterConnected(StompSession session, StompHeaders stompHeaders) {
        log.info("Subscribed : " + session);

        session.subscribe("/topic/messages", this);

        this.session = session;
        //session.send("/app/chat", getSampleMessage()); // почему-то отправляются только из subscribe
    }

    private Response getSampleMessage() {
        return new Response(1L, "cmd1", "Reply message");
    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return Request.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object payload) {
        Request request = (Request) payload;
        log.info("Received : " + request.getCmd() + " id : " + request.getId());

        Response response = new Response(request.getId(), request.getCmd(), "Reply message");
        Message<Response> message = new GenericMessage<>(response);
        //session.send("/topic/messages", response);
        session.send("/app/topic/messages/reply", response);
    }

    @Override
    public void handleException(StompSession session, @Nullable StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {

        log.error("Error: ", exception);
    }
}
