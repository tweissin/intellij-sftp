package com.trw.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.trw.Logger;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * This implements the preferences settings.
 * @author tweissin
 */
public class ConfigSettings implements Configurable {
    public static final String USERNAME = "ssh.username";
    public static final String PASSWORD = "ssh.password";
    public static final String HOST = "ssh.host";
    public static final String DEST_ROOT = "ssh.dest.root";
    public static final String SRC_ROOT = "ssh.src.root";
    public static final String PORT = "ssh.port";
    static final int DEFAULT_SFTP_PORT = 22;
    public static final String STRICT_HOST_KEY_CHECKING = "ssh.strict.host.key.checking";
    static final boolean DEFAULT_STRICT_HOST_KEY_CHECKING = true;
    public static final String DEBUG_LOGGING = "ssh.debug.logging";
    public static final String DEFAULT_DEBUG_LOGGING = "false";
    private static final String PROP_FILENAME = System.getProperty("user.home") + "/intellij-sftp.properties";
    public static Logger LOGGER = new Logger();
    private MySettings settings = new MySettings();

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

    @Nls
    @Override
    public String getDisplayName() {
        return "SFTP";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "myHelpTopic";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        LOGGER.info("loadFieldsFromProperties");
        settings.loadFieldsFromProperties();

        LOGGER.info("Getting component");
        return settings.getComponent();
    }


    @Override
    public boolean isModified() {
        return settings.isModified();
    }


    @Override
    public void apply() throws ConfigurationException {
        LOGGER.info("apply");
        settings.saveFieldsToProperties();
    }

    @Override
    public void reset() {
        LOGGER.info("reset");
        settings.loadFieldsFromProperties();
    }

    @Override
    public void disposeUIResources() {

    }
}
