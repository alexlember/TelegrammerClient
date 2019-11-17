package ru.lember.telegrammerClient.arduino;

import reactor.core.publisher.ReplayProcessor;
import ru.lember.telegrammerClient.dto.in.RequestFromRemote;
import ru.lember.telegrammerClient.dto.inner.ArduinoDataUpdate;

public interface Connector {

    ReplayProcessor<ArduinoDataUpdate> processor();

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
