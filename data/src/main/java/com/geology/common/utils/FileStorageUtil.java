package com.geology.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@Slf4j
public class FileStorageUtil {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.allowed-extensions}")
    private String[] allowedExtensions;

    public String storeFile(MultipartFile file) throws IOException {
        // 验证文件扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

        if (!isExtensionAllowed(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型");
        }

        // 创建上传目录(如果不存在)
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名
        String storedFilename = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(storedFilename);

        // 保存文件
        Files.copy(file.getInputStream(), filePath);

        return storedFilename;
    }

    private boolean isExtensionAllowed(String extension) {
        for (String allowedExt : allowedExtensions) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
