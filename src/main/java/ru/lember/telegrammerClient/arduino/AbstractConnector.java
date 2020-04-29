package ru.lember.telegrammerClient.arduino;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import ru.lember.telegrammerClient.config.ArduinoUpdateProcessor;
import ru.lember.telegrammerClient.config.SerialProperties;
import ru.lember.telegrammerClient.dto.inner.ArduinoDataUpdate;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class AbstractConnector implements Connector {

    private AtomicReference<String> constructedCommand = new AtomicReference<>("");
    ArduinoUpdateProcessor processor;
    SerialProperties serialProperties;

    AbstractConnector(final SerialProperties serialProperties, ArduinoUpdateProcessor processor) {
        this.serialProperties = serialProperties;
        this.processor = processor;
    }

    @PostConstruct
    private void postConstruct() {
        log.info("initialized");

    }

    @Override
    public ArduinoUpdateProcessor processor() {
        return processor;
    }

    @Override
    public synchronized String constructedCommand() {
        return constructedCommand.get();
    }

    private synchronized void append(String s) {
        constructedCommand.set(constructedCommand() + s);
    }

    /**
     * FIXME for mow this method only expects serial cmd only from the single producer.
     */
    public synchronized void constructCmdAndNotify(final String cmdPart) {

        log.info("Getting command part: {}", cmdPart);

        if (StringUtils.isEmpty(cmdPart)) {
            return;
        }

        final String startSymbol = serialProperties.getCmdBeginMarker().toString();

        // before appending
        String buffer = constructedCommand();

        if (cmdPart.startsWith(startSymbol)) {
            if (StringUtils.isEmpty(buffer)) {
                append(cmdPart);
            } else {
                constructedCommand.set(cmdPart);
            }
        } else if (buffer.startsWith(startSymbol)) {
            append(cmdPart);
        }


        while(canSend()) {
            buffer = constructedCommand();

            log.info("buffer: {}", buffer);

            int beginIndex = constructedCommand()
                    .indexOf(startSymbol);
            int endIndex = constructedCommand()
                    .indexOf(serialProperties.getCmdEndMarker());

            String toSend = buffer.substring(beginIndex, endIndex + 1);

            constructedCommand.set(buffer.substring(endIndex + 1));

            if (!StringUtils.isEmpty(buffer)) {
                ArduinoDataUpdate dataUpdate = ArduinoDataUpdate.fromSerialCmd(
                        toSend,
                        startSymbol,
                        serialProperties.getCmdEndMarker().toString(),
                        serialProperties.getCmdSeparator().toString());

                if (dataUpdate != null) {
                    processor.onNext(dataUpdate);
                }
            }
        }

    }

    /**
     * Method checks if it's possible to send command.
     */
    private boolean canSend() {

        if (StringUtils.isEmpty(constructedCommand())) return false;

        int beginIndex = constructedCommand()
                .indexOf(serialProperties.getCmdBeginMarker());
        int endIndex = constructedCommand()
                .indexOf(serialProperties.getCmdEndMarker());

        return beginIndex != -1 && endIndex != -1 && beginIndex < endIndex;
    }


}
