package com.trw.settings;

import com.trw.Logger;
import com.trw.OnFileSaveComponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: tweissin
 * Date: 2/25/13
 * Time: 7:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class MySettings{
    public static Logger LOGGER = new Logger();
    private JPanel panel1;
    private JTextField hostField;
    private JTextField usernameField;
    private JTextField sourceRootField;
    private JTextField destinationRootField;
    private JPasswordField passwordField;
    private JSpinner portField;
    private boolean isModified;
    private static final int DEFAULT_SFTP_PORT = 22;

    public MySettings() {
        LOGGER.info("MySettings()");
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
        hostField.getDocument().addDocumentListener(docl);
        usernameField.getDocument().addDocumentListener(docl);
        sourceRootField.getDocument().addDocumentListener(docl);
        destinationRootField.getDocument().addDocumentListener(docl);
        passwordField.getDocument().addDocumentListener(docl);

        JComponent comp = portField.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        portField.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                isModified=true;
            }
        });

        portField.setModel(new SpinnerNumberModel(DEFAULT_SFTP_PORT,0,Integer.MAX_VALUE,1));
        portField.setEditor(new JSpinner.NumberEditor(portField,"####"));
    }

    public static void main(String[] args) {
        MySettings settings = new MySettings();
        JFrame frame = new JFrame("MySettings");
        frame.setContentPane(settings.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        settings.loadFieldsFromProperties();
    }

    public JComponent getComponent() {
        LOGGER.info("getComponent");
        return panel1;
    }

    public boolean isModified() {
        return isModified;
    }

    public void loadFieldsFromProperties() {
        LOGGER.info("loadFieldsFromProperties");
        Properties props = ConfigSettingsHelper.getConfigProperties();
        if(props!=null) {
            LOGGER.info("load ConfigSettingsHelper.USERNAME");
            usernameField.setText(props.getProperty(ConfigSettingsHelper.USERNAME));
            LOGGER.info("load ConfigSettingsHelper.PASSWORD");
            passwordField.setText(props.getProperty(ConfigSettingsHelper.PASSWORD));
            LOGGER.info("load ConfigSettingsHelper.HOST");
            hostField.setText(props.getProperty(ConfigSettingsHelper.HOST));
            LOGGER.info("load ConfigSettingsHelper.DEST_ROOT");
            destinationRootField.setText(props.getProperty(ConfigSettingsHelper.DEST_ROOT));
            LOGGER.info("load ConfigSettingsHelper.SRC_ROOT");
            sourceRootField.setText(props.getProperty(ConfigSettingsHelper.SRC_ROOT));
            LOGGER.info("load ConfigSettingsHelper.PORT");
            portField.setValue(Integer.valueOf(props.getProperty(ConfigSettingsHelper.PORT)));

        }
    }

    public void saveFieldsToProperties() {
        Properties props = new Properties();
        LOGGER.info("save ConfigSettingsHelper.USERNAME");
        updatePropertyFromField(props, ConfigSettingsHelper.USERNAME, usernameField);
        LOGGER.info("save ConfigSettingsHelper.PASSWORD");
        updatePropertyFromField(props, ConfigSettingsHelper.PASSWORD, passwordField);
        LOGGER.info("save ConfigSettingsHelper.HOST");
        updatePropertyFromField(props, ConfigSettingsHelper.HOST, hostField);
        LOGGER.info("save ConfigSettingsHelper.DEST_ROOT");
        updatePropertyFromField(props, ConfigSettingsHelper.DEST_ROOT, destinationRootField);
        LOGGER.info("save ConfigSettingsHelper.SRC_ROOT");
        updatePropertyFromField(props, ConfigSettingsHelper.SRC_ROOT, sourceRootField);
        LOGGER.info("save ConfigSettingsHelper.portField");
        props.setProperty(ConfigSettingsHelper.PORT, portField.getValue().toString());
        LOGGER.info("save ConfigSettingsHelper.USERNAME");
        ConfigSettingsHelper.saveConfigProperties(props);
        LOGGER.info("save ConfigSettingsHelper.invalidateSshUtil");
        OnFileSaveComponent.invalidateSshUtil();
        LOGGER.info("isModified=false");
        isModified = false;
    }

    private void updatePropertyFromField(Properties props, String fieldName, JTextField field) {
        String value = field.getText();
        props.setProperty(fieldName, value);
    }
}
