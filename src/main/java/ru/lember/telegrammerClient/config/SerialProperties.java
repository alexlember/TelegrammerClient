package ru.lember.telegrammerClient.config;

import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Getter
@Component
public class SerialProperties {

    @Value("${serial.device}")
    private String device;

    @Value("${serial.baud}")
    private Baud baud;

    @Value("${serial.dataBits}")
    private DataBits dataBits;

    @Value("${serial.parity}")
    private Parity parity;

    @Value("${serial.flowControl}")
    private FlowControl flowControl;

    /**
     * Shound be 1 symbol.
     */
    @Value("${serial.cmdBeginMarker:^}")
    private Character cmdBeginMarker;

    /**
     * Shound be 1 symbol.
     */
    @Value("${serial.cmdEndMarker:$}")
    private Character cmdEndMarker;

    @Value("${serial.cmdSeparator:#}")
    private Character cmdSeparator;

    @PostConstruct
    private void postConstruct() {
        log.info("initialized. device: {}, baud: {}, dataBits: {}, parity: {}, flowControl: {}, " +
                        "cmdBeginMarker : {}, cmdEndMarker: {}, cmdSeparator: {}",
                device, baud, dataBits, parity, flowControl, cmdBeginMarker, cmdEndMarker, cmdSeparator);
    }

}
