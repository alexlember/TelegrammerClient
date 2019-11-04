package ru.lember.TelegrammerClient;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    private Long id;
    private String cmd;

}
