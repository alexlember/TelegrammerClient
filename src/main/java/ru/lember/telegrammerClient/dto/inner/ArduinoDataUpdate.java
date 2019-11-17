package ru.lember.telegrammerClient.dto.inner;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.StringUtils;

@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArduinoDataUpdate {

    public static final int VALID_RESPONSE_CMD_PARTS = 6;

    private String cmd;
    private Direction direction;
    private String message;

    @Nullable
    private Long id;


    @Nullable
    public static ArduinoDataUpdate fromSerialCmd(
            String cmd,
            String cmdBeginMarker,
            String cmdEndMarker,
            String cmdSeparator) {

        if (StringUtils.isEmpty(cmd)) {
            return null;
        }

        if (!cmd.startsWith(cmdBeginMarker) || !cmd.endsWith(cmdEndMarker)) {
            return null;
        }

        String[] parts = cmd.split(cmdSeparator);
        if (parts.length != VALID_RESPONSE_CMD_PARTS) {
            return null;
        }

        String cmdName = parts[1];
        String directionString = parts[2];
        String replyId = parts[3];
        String body = parts[4];

        Direction direction = Direction.fromValue(directionString);
        if (direction == null) {
            return null;
        }

        return new ArduinoDataUpdate(cmdName, direction, body, replyId != null ? Long.valueOf(replyId) : null);

    }


}
