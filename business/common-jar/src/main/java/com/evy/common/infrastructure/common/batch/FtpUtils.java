package com.evy.common.infrastructure.common.batch;

import com.evy.common.infrastructure.common.command.BusinessPrpoties;
import com.evy.common.infrastructure.common.command.utils.AppContextUtils;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.evy.common.infrastructure.config.CommandInitialize;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Objects;
import java.util.Vector;
import java.util.function.Consumer;

/**
 * FTP工具类
 *
 * @Author: EvyLiuu
 * @Date: 2019/11/25 20:49
 */
public class FtpUtils {
    private static Session SFTP_SESSION;
    private static int SFTP_TIMEOUT;

    static {
        CommandInitialize.addStaticInitEvent(FtpUtils.class, CommandInitialize.INIT_STATIC_METHOD);
    }

    private static void init(){
        //获取sftp连接超时时间
        BusinessPrpoties prpoties = AppContextUtils.getPrpo();
        SFTP_TIMEOUT = prpoties.getFtp().getLoginTimeout();
    }

    /**
     * 初始化com.jcraft.jsch.Session
     *
     * @throws Exception 连接sftp异常
     */
    private static void initSftpSession() throws Exception {
        BusinessPrpoties prpoties = AppContextUtils.getPrpo();
        String username = prpoties.getFtp().getUsername();
        String password = prpoties.getFtp().getPassword();
        String passphrase = prpoties.getFtp().getPassphrase();
        String privatekey = prpoties.getFtp().getPrivateKey().replaceAll("@@@", "\n");
        String host = prpoties.getFtp().getHost();
        int port = prpoties.getFtp().getPort();

        SFTP_SESSION = returnSftpSession(username, password, privatekey, passphrase, host, port);
    }

    /**
     * 返回已连接的SFTP session
     * @param username   sftp用户名
     * @param password  连接密码
     * @param privateKey sftp私钥
     * @param passphrase    访问私钥的密码
     * @param host       sftp host
     * @param port       sftp端口
     * @return com.jcraft.jsch.Session
     * @throws Exception 连接sftp异常
     */
    private static Session returnSftpSession(String username, String password, String privateKey, String passphrase, String host, int port) throws Exception {
        JSch jSch = new JSch();
        jSch.addIdentity("prk", privateKey.getBytes(), null, StringUtils.isEmpty(passphrase) ? null : passphrase.getBytes());
        Session session = jSch.getSession(username, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        if (!StringUtils.isEmpty(password)) {
            session.setPassword(password);
        }
        session.connect(SFTP_TIMEOUT);
        return session;
    }

    /**
     * 返回一个SFTP通道，用于上传及下载文件
     *
     * @return com.jcraft.jsch.ChannelSftp
     * @throws Exception 连接或打开ChannelSftp异常
     */
    private static ChannelSftp returnSftpChannel() throws Exception {
        if (SFTP_SESSION == null) {
            initSftpSession();
        }

        Channel channel = SFTP_SESSION.openChannel("sftp");
        channel.connect(SFTP_TIMEOUT);
        return (ChannelSftp) channel;
    }

    /**
     * 私有方法，sftp从文服下载文件到本地
     * @param sftp  com.jcraft.jsch.ChannelSftp
     * @param sftpFileName  文服文件名
     * @param localFileName 本地文件名
     */
    private static void download(ChannelSftp sftp, String sftpFileName, String localFileName) {
        CommandLog.info("文服路径:[{}] >> 本地路径:[{}]", sftpFileName, localFileName);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(localFileName))) {
            sftp.get(sftpFileName, bos);
        } catch (Exception e) {
            printDownloadError(e);
        }
    }

    /**
     * 私有方法，从文服下载指定文件到本地
     * @param sftp  com.jcraft.jsch.ChannelSftp
     * @param sftpPath  文服目录路径
     * @param localPath 本地目录路径
     */
    private static void downloadSftp(ChannelSftp sftp, String sftpPath, String localPath) {
        try {
            sftp.cd(sftpPath);
            Vector<ChannelSftp.LsEntry> vector = sftp.ls(sftpPath);
            for (ChannelSftp.LsEntry lsEntry : vector) {
                String fileName = lsEntry.getFilename();
                String localFilePath = localPath + File.separator + fileName;
                String sftpFilePath = sftpPath + File.separator + fileName;
                download(sftp, sftpFilePath, localFilePath);
            }
        } catch (Exception e) {
            printDownloadError(e);
        }
    }

    /**
     * 私有方法，sftp上传本地文件到文服
     * @param sftp  com.jcraft.jsch.ChannelSftp
     * @param sftpFilePath  文服目录
     * @param localFilePath 本地目录
     */
    private static void upload(ChannelSftp sftp, String sftpFilePath, String localFilePath) {
        CommandLog.info("文服路径:[{}] << 本地路径:[{}]", sftpFilePath, localFilePath);
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localFilePath))){
            sftp.put(bis, sftpFilePath);
        } catch (Exception e) {
            printUploadError(e);
        }
    }

    /**
     * 回调一个自动关闭连接的sftp方法执行
     * @param sftpConsumer  com.jcraft.jsch.ChannelSftp
     */
    public static void doSftp(Consumer<ChannelSftp> sftpConsumer) {
        ChannelSftp sftp = null;
        try {
            sftp = returnSftpChannel();
            sftpConsumer.accept(sftp);
        } catch (Exception e) {
            CommandLog.errorThrow("获取SFTP连接异常", e);
        } finally {
            if (sftp != null) {
                sftp.disconnect();
            }
        }
    }

    /**
     * 从文服下载指定目录下所有文件到本地指定目录
     *
     * @param sftpPath  文服目录
     * @param localPath 本地目录
     */
    public static void downloadSftp(String sftpPath, String localPath) {
        doSftp(sftp -> {
            try {
                downloadSftp(sftp, sftpPath, localPath);
            } catch (Exception e) {
                printDownloadError(e);
            }
        });
    }

    /**
     * 从文服下载指定文件到本地
     * @param sftpPath  文服路径
     * @param sftpFileName  文服文件名
     * @param localPath 本地路径
     */
    public static void downloadSftp(String sftpPath, String sftpFileName, String localPath) {
        String sftpFilePath = sftpPath + File.separator + sftpFileName;
        String localFilePath = localPath + File.separator + sftpFileName;
        CommandLog.info("文服路径:[{}] >> 本地路径:[{}]", sftpFilePath, localFilePath);
        doSftp(sftp -> {
            try {
                download(sftp, sftpFilePath, localFilePath);
            } catch (Exception e) {
                printDownloadError(e);
            }
        });
    }

    /**
     * sftp从文服下载文件列表
     * @param host  文服host
     * @param port  文服端口
     * @param username  sftp user
     * @param privateKey    sftp 私钥
     * @param sftpPath      文服路径
     * @param localPath 本地路径
     */
    public static void downloadSftp(String host, int port, String username, String privateKey, String sftpPath, String localPath) {
        Session session = null;
        ChannelSftp sftp = null;
        try {
            session = returnSftpSession(username, null, privateKey, null, host, port);
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect(3000);
            if (sftp.isConnected()) {
                download(sftp, sftpPath, localPath);
            }
        } catch (Exception e) {
            printDownloadError(e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (sftp != null) {
                sftp.disconnect();
            }
        }
    }

    /**
     * sftp从文服下载文件
     * @param host  文服host
     * @param port  文服端口
     * @param username  sftp user
     * @param privateKey    sftp 私钥
     * @param sftpPath      文服路径
     * @param sftpFileName  文服文件名
     * @param localPath 本地路径
     */
    public static void downloadSftp(String host, int port, String username, String privateKey, String sftpPath, String sftpFileName, String localPath) {
        Session session = null;
        ChannelSftp sftp = null;
        try {
            session = returnSftpSession(username, null, privateKey, null, host, port);
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect(3000);
            if (sftp.isConnected()) {
                download(sftp, sftpPath + File.separator + sftpFileName, localPath + File.separator + sftpFileName);
            }
        } catch (Exception e) {
            printDownloadError(e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (sftp != null) {
                sftp.disconnect();
            }
        }
    }

    /**
     * sftp上传本地文件列表到文服
     * @param sftpPath  文服目录
     * @param localPath 本地目录
     * @param localFileName 本地文件名
     */
    public static void upload(String sftpPath, String localPath, String localFileName) {
        String localFilePath = localPath + File.separator + localFileName;
        String sftpFilePath = sftpPath + File.separator + localFileName;
        doSftp(sftp -> {
            try {
                sftp.mkdir(sftpPath);
                upload(sftp, sftpFilePath, localFilePath);
            } catch (Exception e) {
                printUploadError(e);
            }
        });
    }

    /**
     * sftp上传本地文件列表到文服
     * @param sftpPath  文服目录
     * @param localPath 本地目录
     */
    public static void upload(String sftpPath, String localPath) {
        File fileList = new File(localPath);
        if (!fileList.isDirectory()) {
            CommandLog.error("ERR_SFTP_UPLOAD,localPath非本地文件夹");
            return;
        }

        doSftp(sftp -> {
            try {
                for (File file : Objects.requireNonNull(fileList.listFiles())) {
                    String fileName = file.getName();
                    String localFilePath = file.getAbsolutePath();
                    String sftpFilePath = sftpPath + File.separator + fileName;
                    upload(sftp, sftpFilePath, localFilePath);
                }
            } catch (Exception e) {
                printUploadError(e);
            }
        });
    }

    /**
     * 打印上传文件异常日志
     * @param e 异常类
     */
    private static void printUploadError(Exception e){
        CommandLog.errorThrow("上传文件异常", e);
    }

    /**
     * 打印下载文件异常日志
     * @param e 异常类
     */
    private static void printDownloadError(Exception e){
        CommandLog.errorThrow("下载文件异常", e);
    }
}
