package ru.lember.telegrammerClient.dto.in;

import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestFromRemote {

    private Long id;
    private String cmd;
    private String body;
    private Long timeoutMs = 5000L;


    public String toArduinoCmd(final String separator) {
        return cmd + separator + id + separator + body;
    }

}
