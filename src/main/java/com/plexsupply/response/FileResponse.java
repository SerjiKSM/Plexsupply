package com.plexsupply.response;

import org.springframework.http.HttpStatus;

public class FileResponse {
    private String fileName;
    private String fileType;
    private long size;
    private HttpStatus resultStatus;
    private String message;

    private String directory;

    public FileResponse() {
    }

    public FileResponse(String fileName, String fileType, long size, HttpStatus resultStatus, String message) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
        this.resultStatus = resultStatus;
        this.message = message;
    }

    public FileResponse(String directory, String fileName, String fileType, String message, HttpStatus resultStatus) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.message = message;
        this.resultStatus = resultStatus;
        this.directory = directory;
    }

    public FileResponse(String message) {
        this.message = message;
    }

    public HttpStatus getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(HttpStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public static FileResponse error(String errorMessage) {
        return error(errorMessage, HttpStatus.I_AM_A_TEAPOT);
    }

    public static FileResponse error(String errorMessage, HttpStatus httpStatus) {
        FileResponse response = new FileResponse();
        response.setMessage(errorMessage);
        response.setResultStatus(httpStatus);

        return response;
    }

    public static FileResponse directoryInfo(String directory, String fileName, String message) {
        return directoryInfo(directory, fileName, message, HttpStatus.I_AM_A_TEAPOT);
    }

    public static FileResponse directoryInfo(String directory, String fileName, String message, HttpStatus httpStatus) {
        FileResponse response = new FileResponse();
        response.setDirectory(directory);
        response.setFileName(fileName);
        response.setMessage(message);
        response.setResultStatus(httpStatus);

        return response;
    }

    public static FileResponse ok(String message, HttpStatus httpStatus) {
        FileResponse response = new FileResponse();
        response.setMessage(message);
        response.setResultStatus(httpStatus);

        return response;
    }
    
}
