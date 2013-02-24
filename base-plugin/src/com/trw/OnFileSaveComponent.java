package com.trw;

import com.intellij.AppTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

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
                        Project [] projects = ProjectManager.getInstance().getOpenProjects();
                        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                        logSomething("Document being saved: " + file.getCanonicalPath());
                    }
                });
    }

    protected static void logSomething(String msg) {
        try {
            FileWriter pw = new FileWriter("/Users/tweissin/log.txt", true);
            pw.write(new Date().toString() + ": " + msg + "\n");
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void disposeComponent() {
    }
}

