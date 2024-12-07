package com.systems.backend.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.systems.backend.utils.PdfPageExtractor;

@RestController
public class PdfController {

    @GetMapping("/pdf/page")
    public ResponseEntity<byte[]> getPdfPage(
            @RequestParam String pdfPath,
            @RequestParam int pageNumber) {
        try {
            System.out.println("pdfPath: " + pdfPath);
            System.out.println("pageNumber: " + pageNumber);

            byte[] pdfPage = PdfPageExtractor.extractPage(pdfPath, pageNumber);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=page-" + pageNumber + ".pdf");
            headers.add("Content-Type", "application/pdf");

            return new ResponseEntity<>(pdfPage, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/pdf/download")
    public ResponseEntity<FileSystemResource> downloadPdf(@RequestParam String pdfPath) throws IOException {
        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

        File file = new File(uploadDir + File.separator + pdfPath);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
