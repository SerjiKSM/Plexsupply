package com.plexsupply.controller;

import com.plexsupply.response.FileResponse;
import com.plexsupply.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
public class FileController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload-file")
    public FileResponse uploadCSVFile(@RequestParam("file") MultipartFile file) {
        LOGGER.info(this.getClass() + " Method uploadCSVFile");
        if (file.isEmpty()) {
            return FileResponse.error("Select your file!", HttpStatus.I_AM_A_TEAPOT);
        }

        if (!file.getOriginalFilename().endsWith(".csv")) {
            return FileResponse.error("Select your .csv file!", HttpStatus.I_AM_A_TEAPOT);
        }

        String fileName = fileService.saveFile(file);

        return new FileResponse(fileName, file.getContentType(),
                file.getSize(), HttpStatus.CREATED, "File created!");
    }

    @PostMapping("/upload-multiple-files")
    public List<FileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        LOGGER.info(this.getClass() + " Method uploadMultipleFiles");
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadCSVFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/read/folder")
    public List<FileResponse> readDirectory() {
        LOGGER.info(this.getClass() + " Method readDirectory");
        return fileService.readFolder();
    }

    @GetMapping("/search")
    public List<FileResponse> searchFile(@RequestParam("fileName") String fileName) {
        LOGGER.info(this.getClass() + " Method searchFile");
        return fileService.searchFileInDirectory(fileName);
    }

    @PostMapping("/read-file")
    public FileResponse readFile(@RequestParam("readFile") MultipartFile file) {
        LOGGER.info(this.getClass() + " Method readFile ");
        if (file.isEmpty()) {
            return FileResponse.error("Select your file!", HttpStatus.I_AM_A_TEAPOT);
        }

        if (!file.getOriginalFilename().endsWith(".csv")) {
            return FileResponse.error("Select your .csv file!", HttpStatus.I_AM_A_TEAPOT);
        }

        return fileService.readFile(file);
    }

}
