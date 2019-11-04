package ru.lember.TelegrammerClient;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private Long requestId;
    private String cmd;
    private String replyMessage;

}
