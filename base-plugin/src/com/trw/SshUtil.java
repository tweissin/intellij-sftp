package com.trw;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: tweissin
 * Date: 2/23/13
 * Time: 9:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SshUtil {
    private static Logger LOGGER = new Logger();

    public static final String USERNAME = "ssh.username";
    public static final String PASSWORD = "ssh.password";
    public static final String HOST = "ssh.host";
    public static final String DEST_ROOT = "ssh.dest.root";
    public static final String SRC_ROOT = "ssh.src.root";
    public static final String PORT = "ssh.port";

    private String username;
    private String password;
    private String host;
    private String destRoot;
    private String srcRoot;
    private int port;

    public SshUtil(Properties props) {
        username = props.getProperty(USERNAME);
        password = props.getProperty(PASSWORD);
        host = props.getProperty(HOST);
        destRoot = props.getProperty(DEST_ROOT);
        srcRoot = props.getProperty(SRC_ROOT);
        port = Integer.valueOf(props.getProperty(PORT));
        LOGGER.info("Initializing SshUtil");
        LOGGER.info("username: " + username);
        LOGGER.info("password: " + password);
        LOGGER.info("host: " + host);
        LOGGER.info("destRoot: " + destRoot);
        LOGGER.info("srcRoot: " + srcRoot);
        LOGGER.info("port: " + port);
    }


    public void copyFile(InputStream is, String path) {
        String destPath = destRoot + path;
        LOGGER.info("copying file: " + path);
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
}
