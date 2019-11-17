package ru.lember.telegrammerClient.dto.out;

import lombok.*;
import ru.lember.telegrammerClient.dto.ToRemote;


@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestFromRpi implements ToRemote {

    private String cmd;
    private String message;

    @Override
    public String cmd() {
        return cmd;
    }

    @Override
    public String message() {
        return message;
    }
}
