package ru.lember.telegrammerClient.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.messaging.simp.stomp.StompSession;

@Getter
@AllArgsConstructor
public class WsEvent {

    private EventType eventType;
    private @Nullable StompSession session;

}

enum EventType {
    CONNECTION_LOST,
    CONNECTION_ESTABLISHED
}
