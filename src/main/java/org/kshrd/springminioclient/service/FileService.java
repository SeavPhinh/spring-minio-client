package org.kshrd.springminioclient.service;

import io.minio.errors.MinioException;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadFile(MultipartFile file) throws IOException, MinioException;

    Resource findFileByFileName(String fileName) throws MinioException;

    String downloadFile(String fileName) throws MinioException;

    void deleteFile(String fileName);

    String updateFileByFileName(String fileName, MultipartFile file) throws MinioException, IOException, NotFoundException;
}
