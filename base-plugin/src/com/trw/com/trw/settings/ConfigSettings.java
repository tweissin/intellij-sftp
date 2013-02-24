package com.trw.com.trw.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.trw.Logger;
import com.trw.OnFileSaveComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Properties;

/**
 * This implements the preferences settings.
 * @author tweissin
 */
public class ConfigSettings implements Configurable {
    public static Logger LOGGER = new Logger();
    private JTextField usernameField = new JTextField();
    private JTextField passwordField = new JTextField();
    private JTextField hostField = new JTextField();
    private JTextField destinationRootField = new JTextField();
    private JTextField sourceRootField = new JTextField();
    private JTextField portField = new JTextField();
    private boolean isModified = false;

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
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(createPanel());

        loadFieldsFromProperties();
        return panel;
    }

    private void loadFieldsFromProperties() {
        Properties props = ConfigSettingsHelper.getConfigProperties();
        if(props!=null) {
            usernameField.setText(props.getProperty(ConfigSettingsHelper.USERNAME));
            passwordField.setText(props.getProperty(ConfigSettingsHelper.PASSWORD));
            hostField.setText(props.getProperty(ConfigSettingsHelper.HOST));
            destinationRootField.setText(props.getProperty(ConfigSettingsHelper.DEST_ROOT));
            sourceRootField.setText(props.getProperty(ConfigSettingsHelper.SRC_ROOT));
            portField.setText(props.getProperty(ConfigSettingsHelper.PORT));
        }
    }

    private JComponent createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(createComponent("Username:",usernameField));
        panel.add(createComponent("Password:",passwordField));
        panel.add(createComponent("Host:",hostField));
        panel.add(createComponent("Destination Root:",destinationRootField));
        panel.add(createComponent("Source Root:",sourceRootField));
        panel.add(createComponent("Port:",portField));

        DocumentListener docl = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                isModified = true;
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                isModified = true;
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                isModified = true;
            }
        };
        usernameField.getDocument().addDocumentListener(docl);
        passwordField.getDocument().addDocumentListener(docl);
        hostField.getDocument().addDocumentListener(docl);
        destinationRootField.getDocument().addDocumentListener(docl);
        sourceRootField.getDocument().addDocumentListener(docl);
        portField.getDocument().addDocumentListener(docl);

        return panel;
    }

    private JPanel createComponent(String fieldName, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(fieldName), BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public boolean isModified() {
        return isModified;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void apply() throws ConfigurationException {
        Properties props = new Properties();
        updatePropertyFromField(props, ConfigSettingsHelper.USERNAME, usernameField);
        updatePropertyFromField(props, ConfigSettingsHelper.PASSWORD, passwordField);
        updatePropertyFromField(props, ConfigSettingsHelper.HOST, hostField);
        updatePropertyFromField(props, ConfigSettingsHelper.DEST_ROOT, destinationRootField);
        updatePropertyFromField(props, ConfigSettingsHelper.SRC_ROOT, sourceRootField);
        updatePropertyFromField(props, ConfigSettingsHelper.PORT, portField);
        ConfigSettingsHelper.saveConfigProperties(props);
        OnFileSaveComponent.invalidateSshUtil();
        isModified = false;
    }

    private void updatePropertyFromField(Properties props, String fieldName, JTextField field) {
        String value = field.getText();
        props.setProperty(fieldName, value);
    }

    @Override
    public void reset() {
        loadFieldsFromProperties();
    }

    @Override
    public void disposeUIResources() {

    }
}
