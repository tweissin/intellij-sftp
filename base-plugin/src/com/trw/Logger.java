package com.trw;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.trw.settings.ConfigSettings;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * This is a temporary logger until I can get log4j working
 * @author
 */
public class Logger {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Logger.class);
    private static boolean DEBUG = true;
    static {
        // TODO: fix so this works
        String sdebug = ConfigSettings.getConfigProperties().getProperty(ConfigSettings.DEBUG_LOGGING, ConfigSettings.DEFAULT_DEBUG_LOGGING);
        DEBUG = Boolean.valueOf(sdebug);
    }

    public void debug(String message) {
        log("debug", message);
        LOGGER.info(message);
    }

    public void info(String message) {
        log("info", message);
        LOGGER.info(message);
    }

    public void error(String message) {
        log("error", message);
        LOGGER.error(message);
    }

    public void writeToEventLog(String message) {
        Notifications.Bus.notify(new Notification("SFTP", "SFTP", message, NotificationType.ERROR));
    }
    private void log(String level, String msg) {
        if("debug".equals(level) && !DEBUG) {
            return;
        }

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
