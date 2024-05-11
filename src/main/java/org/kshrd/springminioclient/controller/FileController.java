package org.kshrd.springminioclient.controller;

import io.minio.errors.MinioException;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.ibatis.javassist.NotFoundException;
import org.kshrd.springminioclient.model.ApiResponse;
import org.kshrd.springminioclient.model.FileResponse;
import org.kshrd.springminioclient.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

// 1. Upload file
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file to Minio Server")
    public ResponseEntity<ApiResponse<FileResponse>> uploadFile(@RequestParam("file") MultipartFile file) throws MinioException, IOException {
        // save file
        String fileName = fileService.uploadFile(file);
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("files/preview/"+fileName).toUriString();
        // file response
        FileResponse fileResponse = new FileResponse(fileName,
                fileUrl,
                file.getContentType(),
                file.getSize());

        ApiResponse<FileResponse> response = ApiResponse.<FileResponse>builder()
                .status(HttpStatus.CREATED)
                .message("successfully created file")
                .code(201)
                .timestamp(LocalDateTime.now())
                .payload(fileResponse)
                .build();
        return ResponseEntity.ok(response);
    }

// 2. Get preview file
    @GetMapping("/preview/{fileName}")
    @Operation(summary = "Get preview if it's an image file")
    public ResponseEntity<?> getFile(@PathVariable String fileName) throws MinioException {
        Resource resource = fileService.findFileByFileName(fileName);
        MediaType mediaType;
        if (fileName.endsWith(".pdf")){
            mediaType = MediaType.APPLICATION_PDF;
        }if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg") || fileName.endsWith(".gif")){
            mediaType = MediaType.IMAGE_JPEG;
        }else {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
               .header(HttpHeaders.CONTENT_DISPOSITION, "inline; fileName= \"" + fileName + "\"")
                .contentType(mediaType)
                .body(resource);
    }

// 3. Download file from minio sever to specific location
    @GetMapping("/download/{fileName}")
    @Operation(summary = "Download file to specific location")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) throws MinioException {
        String fileDownloadUrl = fileService.downloadFile(fileName);
        return ResponseEntity.ok().build();
    }

// 4. Update file
    @PutMapping(value = "/{fileName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update file by file name")
    public ResponseEntity<?> updateFile(@PathVariable String fileName, @RequestParam("file") MultipartFile file) throws MinioException, IOException, NotFoundException {
        String newFileName = fileService.updateFileByFileName(fileName, file);
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("files/preview/"+newFileName).toUriString();
        FileResponse fileResponse = new FileResponse(newFileName,
                fileUrl,
                file.getContentType(),
                file.getSize());

        ApiResponse<FileResponse> response = ApiResponse.<FileResponse>builder()
                .status(HttpStatus.CREATED)
                .message("successfully created file")
                .code(201)
                .timestamp(LocalDateTime.now())
                .payload(fileResponse)
                .build();
        return ResponseEntity.ok(response);
    }

// 5. Delete file
    @DeleteMapping("/{fileName}")
    @Operation(summary = "Delete file by file name")
    public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
        fileService.deleteFile(fileName);
        ApiResponse<Object> response = ApiResponse.builder()
                .status(HttpStatus.CREATED)
                .message("successfully delete file")
                .code(200)
                .timestamp(LocalDateTime.now())
                .payload(null)
                .build();
        return ResponseEntity.ok(response);
    }

}
