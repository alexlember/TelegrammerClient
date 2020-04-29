package ru.lember.telegrammerClient.arduino;

import lombok.extern.slf4j.Slf4j;
import ru.lember.telegrammerClient.config.ArduinoUpdateProcessorImpl;
import ru.lember.telegrammerClient.config.SerialProperties;
import ru.lember.telegrammerClient.dto.in.RequestFromRemote;
import ru.lember.telegrammerClient.dto.inner.ArduinoDataUpdate;
import ru.lember.telegrammerClient.dto.inner.Direction;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MockConnector extends AbstractConnector {

    private static final String RESPONSE_TEMPLATE = "%s%s%s%srsp%s%s%s%s%s%s";

    private Long mockRequestSendingPeriodSec;
    private Long mockCmdExecutionDelayMs;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private AtomicBoolean currentMode = new AtomicBoolean();
    private AtomicBoolean currentSetupMode = new AtomicBoolean();

    public MockConnector(SerialProperties serialProperties,
                         Long mockRequestSendingPeriodSec,
                         Long mockCmdExecutionDelayMs) {
        super(serialProperties, new ArduinoUpdateProcessorImpl());
        this.mockRequestSendingPeriodSec = mockRequestSendingPeriodSec;
        this.mockCmdExecutionDelayMs = mockCmdExecutionDelayMs;
    }

    @PostConstruct
    private void postConstruct() {
        log.info("initialized");
        //scheduleRandomRequests(); // todo probably get rid of it
    }

    private void scheduleRandomRequests() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            ArduinoDataUpdate update = new ArduinoDataUpdate(
                    "onModeChanged",
                    Direction.REQUEST,
                    "current mode: " + currentMode.getAndSet(!currentMode.get()), null);

            processor.onNext(update);
        },1L, mockRequestSendingPeriodSec, TimeUnit.SECONDS);
    }


    @Override
    public String send(RequestFromRemote requestFromRemote) {

        log.info("RequestFromRemote to Arduino: {}", requestFromRemote);

        delay();

        constructCmdAndNotify(constructFakeResponse(requestFromRemote.getCmd(), String.valueOf(requestFromRemote.getId())));

        return requestFromRemote.toArduinoCmd(serialProperties.getCmdSeparator().toString());
    }

    private void delay() {
        if (mockCmdExecutionDelayMs != null) {
            try {
                Thread.sleep(mockCmdExecutionDelayMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String constructFakeResponse(String cmd, String id) {

        String response = "";
        switch (cmd) {
            case "info":
                response = constructCommand(cmd, id, "time: 23:20:31; timeMode: 1; possible time modes: " +
                        "[withSeconds, noSeconds, secondsOnDetect]; is global setup mode: 0; colorScheme: 3; " +
                        "possible color schemes: [blueLagoon, redDragon, fadeToGray, greenForrest]");
                break;

            case "setup":
                currentSetupMode.set(true);
                response = constructCommand(cmd, id, "Global setup is on.");
                break;

            case "ok":
                currentSetupMode.set(false);
                response = constructCommand(cmd, id, currentSetupMode.get()
                        ? "New time is set. Global setup is off."
                        : "cmd ok is ignored because global setup mode if off.");
                break;

            case "cancel":
                currentSetupMode.set(false);
                response = constructCommand(cmd, id, currentSetupMode.get()
                        ? "Returned to previous time. Global setup is off."
                        : "cancel is ignored because global setup mode if off.");
                break;

            case "time":
                response = constructCommand(cmd, id, "Presetting new time.");
                break;
            case "mode":
                currentMode.getAndSet(!currentMode.get());
                response = constructCommand(cmd, id, "mode switched to: " + currentMode.get());
                break;
            case "color":
                response = constructCommand(cmd, id, "color switched to: 1");
                break;
        }

        return response;

    }

    private String constructCommand(String cmd, String id, String body) {
        return String.format(RESPONSE_TEMPLATE,
                serialProperties.getCmdBeginMarker(), // BEG
                serialProperties.getCmdSeparator(),   // |
                cmd,                                  // info
                serialProperties.getCmdSeparator(),   // |rsp
                serialProperties.getCmdSeparator(),   // |
                id,                                   // 3
                serialProperties.getCmdSeparator(),   // |
                body,                                 // body
                serialProperties.getCmdSeparator(),   // |
                serialProperties.getCmdEndMarker());  // END
    }

}
