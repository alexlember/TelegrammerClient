package ru.lember.telegrammerClient.arduino;

import ru.lember.telegrammerClient.config.ArduinoUpdateProcessor;
import ru.lember.telegrammerClient.dto.in.RequestFromRemote;

public interface Connector {

    ArduinoUpdateProcessor processor();

    /**
     * Methods returns serial command in string
     */
    String send(RequestFromRemote requestFromRemote);

    /**
     * Append part of the command to the result received command.
     * Method is primary for unit testing.
     */
    void constructCmdAndNotify(final String cmdPart);

    /**
     * Get current state of the command being constructed.
     * Method is primary for unit testing.
     */
    String constructedCommand();
}
