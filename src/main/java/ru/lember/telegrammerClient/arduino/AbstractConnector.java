package ru.lember.telegrammerClient.arduino;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.publisher.ReplayProcessor;
import ru.lember.telegrammerClient.config.SerialProperties;
import ru.lember.telegrammerClient.dto.inner.ArduinoDataUpdate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class AbstractConnector implements Connector {

    private AtomicReference<String> constructedCommand = new AtomicReference<>("");
    ReplayProcessor<ArduinoDataUpdate> processor = ReplayProcessor.create(1);

    List<String> possibleCmdBegin;
    List<String> possibleCmdEnd;

    SerialProperties serialProperties;

    AbstractConnector(final SerialProperties serialProperties) {
        this.serialProperties = serialProperties;
        possibleCmdBegin = extractCmd(serialProperties.getCmdBeginMarker(), true);
        possibleCmdEnd = extractCmd(serialProperties.getCmdEndMarker(), false);
    }

    @PostConstruct
    private void postConstruct() {
        log.info("initialized. possibleCmdBegin: {}, possibleCmdEnd: {}",
                possibleCmdBegin, possibleCmdEnd);

    }

    @Override
    public ReplayProcessor<ArduinoDataUpdate> processor() {
        return processor;
    }

    @Override
    public String constructedCommand() {
        return constructedCommand.get();
    }

    /**
     * FIXME for mow this method only expects serial cmd only from the single producer.
     */
    public synchronized void constructCmdAndNotify(final String cmdPart) {

        log.info("Getting command part: {}", cmdPart);

        if (StringUtils.isEmpty(constructedCommand.get())) {
            if (possibleCmdBegin.stream()
                    .anyMatch(cmdPart::startsWith)) {
                constructedCommand.set(constructedCommand.get() + cmdPart);
            }

        } else {

            constructedCommand.set(constructedCommand.get() + cmdPart);

            if (constructedCommand.get().endsWith(serialProperties.getCmdEndMarker())) {

                ArduinoDataUpdate dataUpdate = ArduinoDataUpdate.fromSerialCmd(
                        constructedCommand.get(),
                        serialProperties.getCmdBeginMarker(),
                        serialProperties.getCmdEndMarker(),
                        serialProperties.getCmdSeparator());

                if (dataUpdate != null) {
                    processor.onNext(dataUpdate);
                }

                constructedCommand.set("");
            }
        }

    }

    private List<String> extractCmd(final String cmdMarker, boolean isBegin) {

        List<String> extracted = new ArrayList<>();

        int length = cmdMarker.length();
        int counter = 0;

        while (counter < length) {
            extracted.add(isBegin
                    ? cmdMarker.substring(0, length - counter)
                    : cmdMarker.substring(counter));
            counter++;
        }

        return extracted;
    }


}
