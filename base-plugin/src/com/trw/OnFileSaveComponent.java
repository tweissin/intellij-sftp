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
import com.trw.com.trw.settings.ConfigSettingsHelper;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
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
 *
 * @author tweissin
 */
public class OnFileSaveComponent implements ApplicationComponent {
    private static Logger LOGGER = new Logger();

    private static SshUtil sshUtil;

    @NotNull
    public String getComponentName() {
        return "My On-Save Component";
    }

    public void initComponent() {
        MessageBus bus = ApplicationManager.getApplication().getMessageBus();
        MessageBusConnection connection = bus.connect();
        connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC,
                new FileDocumentManagerAdapter() {
                    @Override
                    public void beforeDocumentSaving(Document document) {
                        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                        ByteArrayInputStream is = new ByteArrayInputStream(document.getText().getBytes());
                        LOGGER.info("About to copy file using OnFileSaveComponent: " + file.getCanonicalPath());
                        sshUtil = getSshUtil();
                        if(sshUtil!=null) {
                            sshUtil.copyFile(is, file.getCanonicalPath());
                            LOGGER.info("Successfully copied file: " + file.getCanonicalPath());
                        } else {
                            LOGGER.error("Couldn't copy file: " + file.getCanonicalPath());
                        }
                    }
                });
    }

    private SshUtil getSshUtil() {
        if(sshUtil==null) {
            Properties props = ConfigSettingsHelper.getConfigProperties();
            if(props==null) {
                LOGGER.error("Problem reading properties...");
                return null;
            }
            sshUtil = new SshUtil(props);
            return sshUtil;
        }
        return sshUtil;
    }

    /**
     * Called when properties change.
     */
    public static void invalidateSshUtil() {
        LOGGER.info("nulling out SshUtil");
        sshUtil = null;
    }

    public void disposeComponent() {
    }
}

