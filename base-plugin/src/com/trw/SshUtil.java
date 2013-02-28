package com.trw;

import com.jcraft.jsch.*;
import com.trw.settings.ConfigSettings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This is used to copy files to a remote destination using SFTP.
 * @author
 */
public class SshUtil {
    private static Logger LOGGER = new Logger();

    private String username;
    private String password;
    private String host;
    private String destRoot;
    private String srcRoot;
    private int port;
    private ChannelSftp sftp;
    private boolean strictHostKeyChecking;

    public SshUtil(Properties props) {
        username = props.getProperty(ConfigSettings.USERNAME);
        password = props.getProperty(ConfigSettings.PASSWORD);
        host = props.getProperty(ConfigSettings.HOST);
        destRoot = props.getProperty(ConfigSettings.DEST_ROOT);
        srcRoot = props.getProperty(ConfigSettings.SRC_ROOT);
        strictHostKeyChecking = Boolean.valueOf(props.getProperty(ConfigSettings.STRICT_HOST_KEY_CHECKING,"true"));
        if(srcRoot.lastIndexOf("/")==srcRoot.length()-1) {
            srcRoot = srcRoot.substring(0,srcRoot.length()-1);
        }
        port = Integer.valueOf(props.getProperty(ConfigSettings.PORT));
        LOGGER.info("Initializing SshUtil");
        LOGGER.info("-> username=" + username);
        LOGGER.info("-> host=" + host);
        LOGGER.info("-> destRoot=" + destRoot);
        LOGGER.info("-> srcRoot=" + srcRoot);
        LOGGER.info("-> port=" + port);
        LOGGER.info("-> strictHostKeyChecking=" + strictHostKeyChecking);
    }

    private void setupSftpChannel() {
        try {
            LOGGER.info("setupSftpChannel");
            JSch jsch = new JSch();
            LOGGER.info("-> jsch.getSession");
            Session session = jsch.getSession(username, host, port);
            java.util.Properties config = new java.util.Properties();
            if(!strictHostKeyChecking) {
                config.put("StrictHostKeyChecking", "no");
            }
            session.setConfig(config);
            LOGGER.info("-> session.setPassword");
            session.setPassword(password);
            LOGGER.info("-> session.connect");
            session.connect();
            LOGGER.info("-> session.openChannel");
            Channel channel = session.openChannel("sftp");
            LOGGER.info("-> channel.connect");
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
            LOGGER.debug("skipping this file: " + path + "; not in srcRoot path: " + srcRoot);
            return;
        }
        String destFile = destRoot + path.substring(srcRoot.length());
        LOGGER.info("copying file: " + path);

        testConnectionAndSetupIfNecessary();

        if(sftp==null) {
            LOGGER.error("sftp is null!? doing nothing");
            return;
        }

        try {
            // Validate remote destination path.
            ensureDestPathExists(sftp, destFile);

            LOGGER.info("-> sftp.put source=" + path + "  dest=" + destFile);
            sftp.put(is, destFile);

            // Don't disconnect with sftp.disconnect();

            LOGGER.debug("-> is.close");
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
        } else {
            setupSftpChannel();
        }
    }

    private void ensureDestPathExists(ChannelSftp sftp, String destFile) throws SftpException {
        String destPath = destFile.substring(0,destFile.lastIndexOf("/"));
        LOGGER.info("ensureDestPathExists destpath=" + destPath);

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
            LOGGER.debug("-> Created dir: " + dirToCreate);
        } catch(SftpException e) {
            LOGGER.debug("-> Dir already exists: " + dirToCreate);
        }

        // find out if at last slash
        endPos = destFile.indexOf("/", endPos);
        String newRootDir = destFile.substring(0,endPos);
        mkRemoteDirs(sftp, newRootDir, destFile);
    }
}
