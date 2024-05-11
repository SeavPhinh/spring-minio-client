package org.kshrd.springminioclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class FileResponse {
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
}