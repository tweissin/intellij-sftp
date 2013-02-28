package com.trw.settings;

import com.trw.Logger;
import com.trw.OnFileSaveComponent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;

/**
 * Settings for plugin
 * @author tweissin
 */
public class MySettings{
    public static Logger LOGGER = new Logger();
    private JPanel panel1;
    private JTextField hostField;
    private JTextField usernameField;
    private JPanel sourceRootFieldPanel;
    private JTextField destinationRootField;
    private JPasswordField passwordField;
    private JSpinner portField;
    private JTextField sourceRootField;
    private JButton sourceRootChooser;
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
        sourceRootChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.setProperty("apple.awt.fileDialogForDirectories", "true");
                FileDialog fileDialog = new FileDialog((Frame)null,"Select source directory");
                String sourceRoot = sourceRootField.getText();
                File sourceRootFile = new File(sourceRoot);
                if(!sourceRootFile.exists()) {
                    sourceRootFile = new java.io.File(System.getProperty("user.home"));
                }
                LOGGER.info("setting cur dir to: " + sourceRootFile);

                fileDialog.setDirectory(sourceRootFile.getAbsolutePath());
                fileDialog.setFilenameFilter(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String s) {
                        return file.isDirectory();
                    }
                });
                fileDialog.setVisible(true);
                sourceRootField.setText(fileDialog.getDirectory());
            }
        });
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
            usernameField.setText(props.getProperty(ConfigSettingsHelper.USERNAME));
            passwordField.setText(props.getProperty(ConfigSettingsHelper.PASSWORD));
            hostField.setText(props.getProperty(ConfigSettingsHelper.HOST));
            destinationRootField.setText(props.getProperty(ConfigSettingsHelper.DEST_ROOT));
            sourceRootField.setText(props.getProperty(ConfigSettingsHelper.SRC_ROOT));
            portField.setValue(Integer.valueOf(props.getProperty(ConfigSettingsHelper.PORT)));

        }
    }

    public void saveFieldsToProperties() {
        Properties props = new Properties();
        updatePropertyFromField(props, ConfigSettingsHelper.USERNAME, usernameField);
        updatePropertyFromField(props, ConfigSettingsHelper.PASSWORD, passwordField);
        updatePropertyFromField(props, ConfigSettingsHelper.HOST, hostField);
        updatePropertyFromField(props, ConfigSettingsHelper.DEST_ROOT, destinationRootField);
        updatePropertyFromField(props, ConfigSettingsHelper.SRC_ROOT, sourceRootField);
        props.setProperty(ConfigSettingsHelper.PORT, portField.getValue().toString());
        ConfigSettingsHelper.saveConfigProperties(props);
        OnFileSaveComponent.invalidateSshUtil();
        LOGGER.info("isModified=false");
        isModified = false;
    }

    private void updatePropertyFromField(Properties props, String fieldName, JTextField field) {
        String value = field.getText();
        props.setProperty(fieldName, value);
    }
}
