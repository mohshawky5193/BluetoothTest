package com.ejada.gurux;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import gurux.dlms.enums.ObjectType;
import gurux.dlms.objects.GXDLMSObjectCollection;
import gurux.io.BaudRate;
import gurux.io.Parity;
import gurux.io.StopBits;
import gurux.net.GXNet;

public class DLMSMeter {

    public static void readMeter(String[] args)
            throws IOException, XMLStreamException {
        Settings settings = new Settings();

        GXDLMSReader reader = null;
        try {
            ////////////////////////////////////////
            // Handle command line parameters.
            int ret = Settings.getParameters(args, settings);
            if (ret != 0) {
                System.exit(1);
                return;
            }

            ////////////////////////////////////////
            // Initialize connection settings.
            if (settings.media instanceof GXSerial2) {
                GXSerial2 serial = (GXSerial2) settings.media;
                if (settings.iec) {
                    serial.setBaudRate(BaudRate.BAUD_RATE_300);
                    serial.setDataBits(7);
                    serial.setParity(Parity.EVEN);
                    serial.setStopBits(StopBits.ONE);
                } else {
                    serial.setBaudRate(BaudRate.BAUD_RATE_9600);
                    serial.setDataBits(8);
                    serial.setParity(Parity.NONE);
                    serial.setStopBits(StopBits.ONE);
                }
            } else if (settings.media instanceof GXNet) {
            } else {
                throw new Exception("Unknown media type.");
            }
            ////////////////////////////////////////
            reader = new GXDLMSReader(settings.client, settings.media,
                    settings.trace, settings.iec, settings.invocationCounter);
            settings.media.open();
            if (!settings.readObjects.isEmpty()) {
                reader.initializeConnection();
                if (settings.outputFile != null
                        && new File(settings.outputFile).exists()) {
                    try {
                        GXDLMSObjectCollection c = GXDLMSObjectCollection
                                .load(settings.outputFile);
                        settings.client.getObjects().addAll(c);
                    } catch (Exception ex) {
                        // It's OK if this fails.
                        System.out.print(ex.getMessage());
                    }
                } else {
                    reader.getAssociationView();
                    if (settings.outputFile != null) {
                        settings.client.getObjects().save(settings.outputFile,
                                null);
                    }
                }
                for (Map.Entry<String, Integer> it : settings.readObjects) {
                    Object val = reader.read(
                            settings.client.getObjects()
                                    .findByLN(ObjectType.NONE, it.getKey()),
                            it.getValue());
                    reader.showValue(it.getValue(), val);
                }
            } else {
                reader.readAll(settings.outputFile);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.exit(1);
                }
            }
            System.out.println("Ended. Press any key to continue.");
        }
    }
}
