package com.trw.settings;

import com.trw.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * This is a helper to load and save properties from disk.
 * @author tweissin
 */
public class ConfigSettingsHelper {
    public static final String USERNAME = "ssh.username";
    public static final String PASSWORD = "ssh.password";
    public static final String HOST = "ssh.host";
    public static final String DEST_ROOT = "ssh.dest.root";
    public static final String SRC_ROOT = "ssh.src.root";
    public static final String PORT = "ssh.port";
    private static Logger LOGGER = new Logger();

    private static final String PROP_FILENAME = System.getProperty("user.home") + "/intellij-sftp.properties";

    public static Properties getConfigProperties() {
        try {
            FileInputStream fis = new FileInputStream(PROP_FILENAME);
            Properties props = new Properties();
            props.load(fis);
            return props;
        } catch (FileNotFoundException e) {
            LOGGER.error("couldn't load properties file: " + PROP_FILENAME + ": " + e.getMessage());
            return null;
        } catch (IOException e) {
            LOGGER.error("couldn't load properties file: " + PROP_FILENAME + ": " + e.getMessage());
            return null;
        }

    }

    public static void saveConfigProperties(Properties props) {
        try {
            LOGGER.info("Saving properties to " + PROP_FILENAME);
            FileWriter fw = new FileWriter(PROP_FILENAME);
            props.store(fw,null);
            fw.close();
        } catch (IOException e) {
            LOGGER.error("Failed to save properties to " + PROP_FILENAME + "; " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
