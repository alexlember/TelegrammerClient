package ru.lember.TelegrammerClient.arduinoConnector;

import com.pi4j.io.serial.*;
import com.pi4j.util.Console;

import java.io.IOException;

public class Connector {

    private void connect() throws InterruptedException {
        final Console console = new Console();

        // print program title/header
        System.out.println("<-- The Pi4J Project -->" + "Serial Communication Example");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // create an instance of the serial communications class
        final Serial serial = SerialFactory.createInstance();

        // create and register the serial data listener
        serial.addListener(new SerialDataEventListener() {
            @Override
            public void dataReceived(SerialDataEvent event) {

                // print out the data received to the console
                try {
                    System.out.println("[HEX DATA]   " + event.getHexByteString());
                    System.out.println("[ASCII DATA] " + event.getAsciiString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            SerialConfig config = new SerialConfig();

            config.device("/dev/rfcomm2")
                    .baud(Baud._9600)
                    .dataBits(DataBits._8)
                    .parity(Parity.NONE)
                    // .dataBits(StopBits._1)
                    .flowControl(FlowControl.NONE);

//                        if(args.length > 0){
//                            config = CommandArgumentParser.getSerialConfig(config, args);
//                        }

            System.out.println(" Connecting to: " + config.toString() +
                    " We are sending ASCII data on the serial port every 1 second." +
                    " Data received on serial port will be displayed below.");


            // open the default serial device/port with the configuration settings
            serial.open(config);

            // continuous loop to keep the program running until the user terminates the program
            while(console.isRunning()) {
                try {
                    //serial.write("info");
//                                        // write a formatted string to the serial transmit buffer
//                                        serial.write("CURRENT TIME: " + new Date().toString());
//
//                                        // write a individual bytes to the serial transmit buffer
//                                        serial.write((byte) 13);
//                                        serial.write((byte) 10);
//
//                                        // write a simple string to the serial transmit buffer
//                                        serial.write("Second Line");
//
//                                        // write a individual characters to the serial transmit buffer
//                                        serial.write('\r');
//                                        serial.write('\n');
//
//                                        // write a string terminating with CR+LF to the serial transmit buffer
//                                        serial.writeln("Third Line");
                }
                catch(IllegalStateException ex){
                    ex.printStackTrace();
                }

                // wait 1 second before continuing
                Thread.sleep(1000);
            }

        }
        catch(IOException ex) {
            System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
        }
    }
}
