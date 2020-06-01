package com.plexsupply.service;

import com.plexsupply.exceptions.FileException;
import com.plexsupply.response.FileResponse;
import com.plexsupply.property.FileProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FileService {
    private FtpService ftpService;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final Path fileLocation;
    private List<FileResponse> directoryInfo = new ArrayList<FileResponse>();
    private List<FileResponse> searchInfo = new ArrayList<FileResponse>();
    private Boolean searchResult = false;

    @Autowired
    public FileService(FileProperties fileProperties, FtpService ftpService) {
        this.ftpService = ftpService;
        this.fileLocation = Paths.get(fileProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileLocation);
        } catch (Exception ex) {
            LOGGER.error(this.getClass() + " Constructor Error create the directory");
            throw new FileException("Can not create the directory.", ex);
        }
    }

    public String saveFile(MultipartFile file) {
        LOGGER.info(this.getClass() + " Method saveFile");
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileException("Error! File name contains invalid path sequence " + fileName);
            }

            Path targetLocation = this.fileLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            LOGGER.error(this.getClass() + " Method saveFile Error save file");
            throw new FileException("Error save file " + fileName + ". Try again!", ex);
        }
    }

    public List<FileResponse> readFolder() {
        LOGGER.info(this.getClass() + " Method readFolder");
        directoryInfo.clear();
        String dirPath = String.valueOf(fileLocation);
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files.length == 0) {
            System.out.println("Directory is empty!");
            return Collections.singletonList(FileResponse.error("Directory is empty!", HttpStatus.I_AM_A_TEAPOT));
        } else {
            for (File file : files) {
                fetchDirectoryData(file);
            }
        }
        return directoryInfo;
    }


    public List<FileResponse> fetchDirectoryData(File file) {
        if (file.isDirectory()) {

            File[] children = file.listFiles();

            if (children.length == 0) {
                return null;
            }

            for (File child : children) {
                this.fetchDirectoryData(child);
            }
        } else {
            System.out.println(file);
            directoryInfo.add(FileResponse.directoryInfo(file.getParent(), file.getName(), "Directory data!"));
        }
        return directoryInfo;
    }

    public List<FileResponse> searchFileInDirectory(String fileName) {
        LOGGER.info(this.getClass() + " Method searchFileInDirectory");
        searchInfo.clear();
        searchResult = false;
        String dirPath = String.valueOf(fileLocation);
        File directory = new File(dirPath);
        if (directory.isDirectory()) {
            return searchFile(directory, fileName);
        } else {
            System.out.println(directory.getAbsoluteFile() + " is not a directory!");
            return Collections.singletonList(FileResponse.error(directory.getAbsoluteFile() + " is not a directory!"));
        }
    }

    private List<FileResponse> searchFile(File file, String fileNameToSearch) {
        for (File temp : file.listFiles()) {
            if (temp.isDirectory()) {
                searchFile(temp, fileNameToSearch);
            } else {
                if (fileNameToSearch.equals(temp.getName())) {
                    System.out.println(fileNameToSearch + " file is found!");
                    searchResult = true;
                    String fileExtension = temp.getName().substring(temp.getName().length() - 4);
                    searchInfo.add(new FileResponse(temp.getParent(),
                            temp.getName(), fileExtension, "File is found!", HttpStatus.OK));
                }
            }
        }

        if (searchResult) {
            return searchInfo;
        } else {
            return Collections.singletonList(FileResponse.error(fileNameToSearch + " is not found!", HttpStatus.NOT_FOUND));
        }
    }

    public FileResponse readFile(MultipartFile file) {
        LOGGER.info(this.getClass() + " Method readFile");
        String dirPath = String.valueOf(fileLocation);

        try (BufferedReader in = new BufferedReader(new FileReader(dirPath + "/" + file.getOriginalFilename()))) {
            String str;
            LOGGER.info(this.getClass() + " Method readFile start read data");
            while ((str = in.readLine()) != null) {
                System.out.println(str);
            }

            ftpService.uploadFileToFtpServer(dirPath, file.getOriginalFilename());

        } catch (IOException e) {
            LOGGER.error(this.getClass() + " Method readFile Error read data");
            return FileResponse.error("File " + file.getOriginalFilename() + " is not found!", HttpStatus.NOT_FOUND);
        }
        LOGGER.info(this.getClass() + " Method readFile finish read data");
        return FileResponse.ok("All lines of the file are displayed in the console", HttpStatus.OK);
    }

}
