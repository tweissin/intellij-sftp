package com.trw;

import com.trw.settings.ConfigSettings;
import org.junit.Test;

import java.io.*;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: tweissin
 * Date: 2/23/13
 * Time: 9:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class SshUtilTest {

    @Test
    public void testCopy() throws IOException {
        FileInputStream fis = new FileInputStream("intellij-sftp.properties");
        Properties props = new Properties();
        props.load(fis);

        String path = "/Container.jsp";

        SshUtil sshUtil = new SshUtil(props);
        sshUtil.copyFile(getInputStream(props, path), props.getProperty(ConfigSettings.SRC_ROOT) + path);
    }

    @Test
    public void testMultipleCopies() throws IOException {
        FileInputStream fis = new FileInputStream("intellij-sftp.properties");
        Properties props = new Properties();
        props.load(fis);


        SshUtil sshUtil = new SshUtil(props);
        String path;

        path = "/Container.jsp";
        sshUtil.copyFile(getInputStream(props, path), props.getProperty(ConfigSettings.SRC_ROOT) + path);
        path = "/js/cce/mvc-base.js";
        sshUtil.copyFile(getInputStream(props, path), props.getProperty(ConfigSettings.SRC_ROOT) + path);
    }

    @Test
    public void testCopyWithSubdir() throws IOException {
        FileInputStream fis = new FileInputStream("intellij-sftp.properties");
        Properties props = new Properties();
        props.load(fis);

        String path = "/js/cce/mvc-base.js";

        SshUtil sshUtil = new SshUtil(props);
        sshUtil.copyFile(getInputStream(props, path), props.getProperty(ConfigSettings.SRC_ROOT) + path);
    }

    @Test
    public void testCopyFileWeDontCareAbout() throws IOException {
        FileInputStream fis = new FileInputStream("intellij-sftp.properties");
        Properties props = new Properties();
        props.load(fis);

        String path = "/js/cce/mvc-base.js";

        SshUtil sshUtil = new SshUtil(props);
        sshUtil.copyFile(getInputStream(props, path), "/tmp/" + path);
    }

    private InputStream getInputStream(Properties props, String filename) throws FileNotFoundException {
        String srcRoot = props.getProperty(ConfigSettings.SRC_ROOT);
        String srcFilename = srcRoot + filename;
        FileInputStream is = new FileInputStream(srcFilename);
        return is;
    }
}
