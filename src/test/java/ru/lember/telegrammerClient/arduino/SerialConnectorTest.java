package ru.lember.telegrammerClient.arduino;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.lember.telegrammerClient.dto.in.RequestFromRemote;
import ru.lember.telegrammerClient.dto.inner.Direction;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SerialConnectorTestConfiguration.class)
class SerialConnectorTest {

    @Autowired
    private Connector connector;

    private static final String EXPECTED_INFO_BODY = "time: 23:20:31; timeMode: 1; possible time modes: [withSeconds, noSeconds, secondsOnDetect]; is global setup mode: 0; colorScheme: 3; possible color schemes: [blueLagoon, redDragon, fadeToGray, greenForrest]";


    @Test
    void sendRequestTest() {
        RequestFromRemote requestFromRemote = new RequestFromRemote(1L, "info", "", 5000L);
        Assertions.assertEquals("info%1%", connector.send(requestFromRemote));
    }

    @Test
    void connectorTest() {

        CountDownLatch latch = new CountDownLatch(1);

        connector.processor()
                .filter(arduinoDataUpdate ->
                        "info".equals(arduinoDataUpdate.getCmd())
                                && new Long(1).equals(arduinoDataUpdate.getId())
                )
                .timeout(Duration.ofMillis(500))
                .subscribe(e -> {

                    Assertions.assertEquals(1L, e.getId());
                    Assertions.assertEquals("info", e.getCmd());
                    Assertions.assertEquals(Direction.RESPONSE, e.getDirection());
                    Assertions.assertEquals(EXPECTED_INFO_BODY, e.getMessage());

                    latch.countDown();
                }, t -> Assertions.fail("event bus error"));

        connector.constructCmdAndNotify("~");
        connector.constructCmdAndNotify("~");
        connector.constructCmdAndNotify("~");
        connector.constructCmdAndNotify("~");

        Assertions.assertEquals("", connector.constructedCommand());

        connector.constructCmdAndNotify("#");
        Assertions.assertEquals("#", connector.constructedCommand());
        connector.constructCmdAndNotify("#");
        Assertions.assertEquals("##", connector.constructedCommand());
        connector.constructCmdAndNotify("#%info%rsp%1%");
        Assertions.assertEquals("###%info%rsp%1%", connector.constructedCommand());
        connector.constructCmdAndNotify(EXPECTED_INFO_BODY);
        Assertions.assertEquals("###%info%rsp%1%" + EXPECTED_INFO_BODY, connector.constructedCommand());
        connector.constructCmdAndNotify("%>");
        Assertions.assertEquals("###%info%rsp%1%" + EXPECTED_INFO_BODY + "%>", connector.constructedCommand());

        connector.constructCmdAndNotify(">>");
        Assertions.assertEquals("", connector.constructedCommand());

        Assertions.assertTimeout(Duration.ofSeconds(1), () -> {
            try {
                latch.await(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
