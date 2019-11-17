package ru.lember.telegrammerClient.dto.in;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import ru.lember.telegrammerClient.dto.ToRemote;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseToRemote implements ToRemote {

    private Long correlationId;
    private String cmd;
    private @Nullable String message;

    @Override
    public String cmd() {
        return cmd;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public Long correlationId() {
        return correlationId;
    }
}