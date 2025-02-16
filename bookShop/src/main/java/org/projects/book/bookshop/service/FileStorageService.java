package org.projects.book.bookshop.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
public class FileStorageService {

    @Value("${application.file.upload.photo-output-path}")
    private String fileUploadPath;

    public String saveFile(@NonNull MultipartFile sourceFile, @NonNull Long userId) {
        final String fileUploadSubPath = "users" + File.separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }

    private String uploadFile(@NonNull MultipartFile sourceFile, @NonNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        //directory
        final File uploadDir = new File(finalUploadPath);
        if (!uploadDir.exists()) {
            boolean folderCreated = uploadDir.mkdirs();
            if (!folderCreated) {
                log.warn("failed to create upload directory");
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Path.of(targetFilePath);
        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("file saved at {}", targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("file not saved", e);
        }
        return null;
    }


    private String getFileExtension(String originalFilename) {
        if(originalFilename==null||originalFilename.isEmpty()){
            return "";
        }
        final int lastIndexOfDot = originalFilename.lastIndexOf('.');
        if(lastIndexOfDot==-1){
            return "";
        }
        return originalFilename.substring(lastIndexOfDot+1).toLowerCase();
    }
}
