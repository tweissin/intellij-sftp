package com.trw;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.jcraft.jsch.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: tweissin
 * Date: 2/23/13
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextBoxes extends AnAction {
    private static Logger LOGGER = new Logger();

    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.
    public TextBoxes() {
        // Set the menu item name.
        super("Text _Boxes");
        // Set the menu item name, description and icon.
        // super("Text _Boxes","Item description",IconLoader.getIcon("/Mypackage/icon.png"));
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
        if("1".equals(txt)) {
            displayMsg(project, "About to copy file...");
            copyFile();
            displayMsg(project, "...Copied file");
        } else {
            displayMsg(project, "Hello, " + txt + "!\n I am glad to see you.");
        }
    }

    private void displayMsg(Project project, String msg) {
        Messages.showMessageDialog(project, msg, "Information", Messages.getInformationIcon());
    }

    /**
     * http://epaul.github.com/jsch-documentation/javadoc/
     * http://stackoverflow.com/questions/14617/java-what-is-the-best-way-to-sftp-a-file-from-a-server
     */
    private void copyFile() {
        String username = "boston";
        String password = "C1sco123=";
        String host = "10.86.135.221";
        String localFile = "";
        String destPath = "/cygdrive/c/temp/foo.txt";
        int port = 3022;
        JSch jsch = new JSch();

        try {
            LOGGER.info("jsch.getSession");
            Session session = jsch.getSession(username, host, port);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            LOGGER.info("session.setPassword");
            session.setPassword(password);
            LOGGER.info("session.connect");
            session.connect();
            LOGGER.info("session.openChannel");
            Channel channel = session.openChannel("sftp");
            LOGGER.info("channel.connect");
            channel.connect();
            LOGGER.info("getInputStream");
            ChannelSftp sftp = (ChannelSftp)channel;
            InputStream is = getInputStream(localFile);
            LOGGER.info("sftp.put");
            sftp.put(is, destPath);
            LOGGER.info("sftp.disconnect");
            sftp.disconnect();
            LOGGER.info("is.close");
            is.close();
        } catch (JSchException e) {
            LOGGER.info("JSchException: " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SftpException e) {
            LOGGER.info("SftpException: " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            LOGGER.info("IOException: " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private InputStream getInputStream(String filename) {
        String data = "hello, world";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes());
        return is;
    }
}
