package com.plexsupply.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

import org.apache.commons.net.ftp.FTPClient;

@Service
public class FtpService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final String SERVER = "";
    private final int PORT = 21;
    private final String USER = "";
    private final String PASS = "";

    private static final int BUFFER_SIZE = 4096;

    public void uploadFileToFtpServer(String dirPath, String filename) {
        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(SERVER, PORT);
            ftpClient.login(USER, PASS);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            File localFile = new File(dirPath + "/" + filename);
            String remoteFile = filename;
            InputStream inputStream = new FileInputStream(localFile);

            LOGGER.info(this.getClass() + " Start uploading file to FTP");
            OutputStream outputStream = ftpClient.storeFileStream(remoteFile);
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;

            while ((read = inputStream.read(bytesIn)) != -1) {
                outputStream.write(bytesIn, 0, read);
            }
            inputStream.close();
            outputStream.close();

            boolean completed = ftpClient.completePendingCommand();
            if (completed) {
                LOGGER.info(this.getClass() + " File is uploaded to FTP successfully");
            }

        } catch (IOException ex) {
            LOGGER.error(this.getClass() + " Error uploaded to FTP " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                LOGGER.error(this.getClass() + " Error with FTP " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
