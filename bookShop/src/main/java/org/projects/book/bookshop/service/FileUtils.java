package org.projects.book.bookshop.service;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtils {

    public static byte[] readFileFromLocation(String fileUrl) {
        if(StringUtils.isBlank(fileUrl)) {
            return null;
        }
        try {
            Path path = new File(fileUrl).toPath();
            return Files.readAllBytes(path);
        }
        catch (Exception e) {
            log.error("error reading file from location {}", fileUrl, e);
        }
        return null;
    }
}
