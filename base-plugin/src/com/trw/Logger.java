package com.trw;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * This is a temporary logger until I can get log4j working
 * @author
 */
public class Logger {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Logger.class);

    public void info(String message) {
        log("info", message);
        LOGGER.info(message);
    }

    public void error(String message) {
        log("error", message);
        LOGGER.error(message);
    }
    private void log(String level, String msg) {
        try {
            FileWriter pw = new FileWriter(System.getProperty("user.home") + "/intellij-sftp.log", true);
            String formattedMsg = new Date().toString() + " [" + level + "] " + msg;
            pw.write(formattedMsg + "\n");
            System.out.println(formattedMsg);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
