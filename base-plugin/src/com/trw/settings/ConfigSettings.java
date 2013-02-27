package com.trw.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.trw.Logger;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * This implements the preferences settings.
 * @author tweissin
 */
public class ConfigSettings implements Configurable {
    public static Logger LOGGER = new Logger();
    private MySettings settings = new MySettings();

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
