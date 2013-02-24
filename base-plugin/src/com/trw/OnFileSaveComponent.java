package com.trw;

import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * See this for how to do something when saving
 * http://arhipov.blogspot.com/2011/04/code-snippet-intercepting-on-save.html#
 *
 * FAQ:
 * http://confluence.jetbrains.com/display/IDEADEV/Plugin+Development+FAQ
 *
 * tips:
 * http://tomaszdziurko.pl/2011/09/developing-plugin-intellij-idea-some-tips-and-links/
 *
 * arch:
 * http://confluence.jetbrains.com/display/IDEADEV/IntelliJ+IDEA+Architectural+Overview
 *
 * also:
 * cp /Users/tweissin/source/intelliJ/tom-plugin/tom-plugin.jar "/Applications/IntelliJ IDEA 12 CE.app/plugins"
 */
public class OnFileSaveComponent implements ApplicationComponent {
    private static Logger LOGGER = new Logger();

    private SshUtil sshUtil;

    @NotNull
    public String getComponentName() {
        return "My On-Save Component";
    }

    public void initComponent() {
        String propFile = System.getProperty("user.home") + "/intellij-sftp.properties";
        try {
            FileInputStream fis = new FileInputStream(propFile);
            Properties props = new Properties();
            props.load(fis);
            sshUtil = new  SshUtil(props);
        } catch (FileNotFoundException e) {
            LOGGER.error("couldn't load properties file: " + propFile + ": " + e.getMessage());
            return;
        } catch (IOException e) {
            LOGGER.error("couldn't load properties file: " + propFile + ": " + e.getMessage());
            return;
        }
        MessageBus bus = ApplicationManager.getApplication().getMessageBus();
        MessageBusConnection connection = bus.connect();
        connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC,
                new FileDocumentManagerAdapter() {
                    @Override
                    public void beforeDocumentSaving(Document document) {
                        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                        ByteArrayInputStream is = new ByteArrayInputStream(document.getText().getBytes());
                        LOGGER.info("About to copy file using OnFileSaveComponent: " + file.getCanonicalPath());
                        sshUtil.copyFile(is, file.getCanonicalPath());
                        LOGGER.info("Successfully copied file: " + file.getCanonicalPath());
                    }
                });
    }

    public void disposeComponent() {
    }
}

