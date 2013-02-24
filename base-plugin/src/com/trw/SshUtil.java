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
    private ChannelSftp sftp;

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

        setupSftpChannel();
    }

    private void setupSftpChannel() {
        try {
            JSch jsch = new JSch();
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

            // "Cache" the SFTP connection
            sftp = (ChannelSftp)channel;
        } catch (JSchException e) {
            LOGGER.info("JSchException: " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void copyFile(InputStream is, String path) {
        if(path.indexOf(srcRoot)==-1) {
            // do nothing
            LOGGER.info("skipping this file: " + path + "; not in srcRoot path: " + srcRoot);
            return;
        }
        String destFile = destRoot + path.substring(srcRoot.length());
        LOGGER.info("copying file: " + path);

        testConnectionAndSetupIfNecessary();

        try {
            // Validate remote destination path.
            ensureDestPathExists(sftp, destFile);

            LOGGER.info("sftp.put");
            sftp.put(is, destFile);

            // Don't disconnect with sftp.disconnect();

            LOGGER.info("is.close");
            is.close();
        } catch (SftpException e) {
            LOGGER.info("SftpException: " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            LOGGER.info("IOException: " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void testConnectionAndSetupIfNecessary() {
        if(sftp!=null) {
            try {
                sftp.ls("/");
            } catch (SftpException e) {
                LOGGER.info("Channel disconnected?  Re-establishing SFTP channel...");
                setupSftpChannel();
            }
        }
    }

    private void ensureDestPathExists(ChannelSftp sftp, String destFile) throws SftpException {
        String destPath = destFile.substring(0,destFile.lastIndexOf("/"));
        LOGGER.info("destpath=" + destPath);

        try {
            sftp.ls(destPath);
        } catch (SftpException e) {
            // Start at root and keep making dirs until we get to the end
            LOGGER.info("no such path: " + destPath + "; " + e.getMessage());
            mkRemoteDirs(sftp, destRoot, destFile);
        }
    }

    /**
     * Recursively creates directories given the specified path.
     */
    private static void mkRemoteDirs(ChannelSftp sftp, String rootDir, String destFile) throws SftpException {
        int startPos = rootDir.length() + 1;
        int endPos = destFile.indexOf("/", startPos);
        if(endPos==-1) {
            return;
        }
        String nextDir = destFile.substring(startPos, endPos);
        String dirToCreate = rootDir + "/" + nextDir;
        try {
            sftp.mkdir(dirToCreate);
            LOGGER.info("Created dir: " + dirToCreate);
        } catch(SftpException e) {
            LOGGER.info("Dir already exists: " + dirToCreate);
        }

        // find out if at last slash
        endPos = destFile.indexOf("/", endPos);
        String newRootDir = destFile.substring(0,endPos);
        mkRemoteDirs(sftp, newRootDir, destFile);
    }
}
