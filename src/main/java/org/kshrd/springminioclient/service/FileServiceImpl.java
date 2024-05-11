package org.kshrd.springminioclient.service;

import io.minio.*;
import io.minio.errors.MinioException;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.ibatis.javassist.NotFoundException;
import org.kshrd.springminioclient.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Value("${minio.bucketName}")
    private String bucketName;

    private final MinioClient minioClient;
    private final FileRepository fileRepository;

    public FileServiceImpl(MinioClient minioClient, FileRepository fileRepository) {
        this.minioClient = minioClient;
        this.fileRepository = fileRepository;
    }

    @Override
    public String uploadFile(MultipartFile file) throws MinioException, IOException {
        //1. cat.jpg
        String fileName = file.getOriginalFilename();
        try (InputStream stream = file.getInputStream()) {
            fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(fileName);

            //1. upload from any source and provide data as an InputStream
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(stream, stream.available(), -1)
                    .build());

            //2. upload file from specific local file path in your file system
//            String localFilePath = "src/main/resources/images/40542.jpg";
//            minioClient.uploadObject(
//                    UploadObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object("40542.jpg")
//                            .filename(localFilePath)
//                            .contentType(String.valueOf(MediaType.IMAGE_PNG))
//                            .build());
            fileRepository.saveFile(fileName);
            stream.close();
            return fileName;
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new MinioException("Failed to upload file");
        }
    }

    @Override
    public Resource findFileByFileName(String fileName) throws MinioException {
        try {
            String file = fileRepository.findFileByName(fileName);
            if (file ==null) {
                throw new NotFoundException("File not found");
            }
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
            byte[] content = IOUtils.toByteArray(stream);
            // Close the InputStream
            stream.close();
            return new ByteArrayResource(content);
        }catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException | NotFoundException e) {
            throw new MinioException("Failed to fetch file");
//            throw new RuntimeException(e);
        }

    }


// download from minio sever to specific location
    @Override
    public String downloadFile(String fileName){
        try {
            String file = fileRepository.findFileByName(fileName);
            if (file == null) {
                throw new NotFoundException("File not found");
            }
            String savePath = "src/main/resources/images/" + fileName;
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .filename(savePath)
                            .build());
            System.out.println("File downloaded successfully to: " + savePath);
            return "success";
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

// delete file
    @Override
    public void deleteFile(String fileName) {
        try {
            String file = fileRepository.findFileByName(fileName);
            if (file == null) {
                throw new NotFoundException("File not found");
            }
            fileRepository.deleteFile(fileName);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String updateFileByFileName(String fileName, MultipartFile file) throws MinioException, IOException, NotFoundException {

        if (fileRepository.findFileByName(fileName) == null) {
            throw new NotFoundException("File not found");
        }
        deleteFile(fileName);
        String newFilename = uploadFile(file);
        fileRepository.updateFile(newFilename);
        return newFilename;
    }


}
