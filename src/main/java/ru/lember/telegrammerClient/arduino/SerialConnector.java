package ru.lember.telegrammerClient.arduino;

import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;
import lombok.extern.slf4j.Slf4j;
import ru.lember.telegrammerClient.config.ArduinoUpdateProcessor;
import ru.lember.telegrammerClient.config.SerialProperties;
import ru.lember.telegrammerClient.dto.in.RequestFromRemote;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Slf4j
public class SerialConnector extends AbstractConnector {

    private boolean isInTestMode;
    private Serial serial;

    public SerialConnector(SerialProperties serialProperties, ArduinoUpdateProcessor processor, boolean isInTestMode) {
        super(serialProperties, processor);
        this.isInTestMode = isInTestMode;
    }

    @PostConstruct
    private void postConstruct() {
        log.info("initialized. isInTestMode: {}", isInTestMode);

        if (!isInTestMode) {
            log.info("Starting serial connection");
            start();
        }
    }

    @Override
    public String send(RequestFromRemote data) {

        String cmd = data.toArduinoCmd(serialProperties.getCmdSeparator().toString());
        log.info("Sending data: {} via serial interface", cmd);

        if (!isInTestMode) {
            if (serial.isOpen()) {
                try {
                    serial.write(cmd);
                } catch (IOException e) {
                    throw new RuntimeException("Error while sending serial data: " + e);
                }
            } else {
                log.warn("The data: {} was not send, the serial is not opened.", data);
            }
        }

        return cmd;
    }

    private void start() {

        serial = SerialFactory.createInstance();
        SerialConfig config = new SerialConfig();

        config.device(serialProperties.getDevice())
                .baud(serialProperties.getBaud())
                .dataBits(serialProperties.getDataBits())
                .parity(serialProperties.getParity())
                .flowControl(serialProperties.getFlowControl());

        serial.addListener((SerialDataEventListener) event -> {

            try {

                String cmdPart = event.getAsciiString();
                constructCmdAndNotify(cmdPart);

            } catch (IOException e) {
                log.error("Error while serial data receive event: {}", e.toString());
            }
        });

        try {
            serial.open(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Serial connection is opened");

    }

    @PreDestroy
    private void preDestroy() {
        log.info("destroying");
        if (!isInTestMode) {
            try {
                serial.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
