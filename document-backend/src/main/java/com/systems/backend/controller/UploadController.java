package com.systems.backend.controller;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @Value("${upload.dir}")
    private String uploadDir;

    @GetMapping("/thumbnail/{filename}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable String filename)
            throws MalformedURLException, FileNotFoundException {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename); 
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(resource);
            } else {
                throw new FileNotFoundException("File thumbnail không tồn tại: " + filePath.toString());
            }
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ByteArrayResource(("Không tìm thấy file: " + filename).getBytes()));
        }
    }

    @GetMapping("/content/{filename}")
    public ResponseEntity<Resource> getContent(@PathVariable String filename)
            throws MalformedURLException, FileNotFoundException {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename); // Đường dẫn đến file content
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(resource);
            } else {
                throw new FileNotFoundException("File content không tồn tại: " + filePath.toString());
            }
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ByteArrayResource(("Không tìm thấy file: " + filename).getBytes()));
        }
    }
}
