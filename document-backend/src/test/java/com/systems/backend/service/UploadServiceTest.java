package com.systems.backend.service;

import com.systems.backend.service.impl.UploadServiceImpl;
import com.systems.backend.utils.UploadResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadServiceTest {
    private static final String UPLOAD_DIR = "uploads/";

    @InjectMocks
    private UploadServiceImpl uploadService;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private PDDocument mockDocument;

    @Mock
    private PDPageTree mockPageTree;

    @Mock
    private PDPage mockPage;

    @Mock
    private BufferedImage mockImage;

    @BeforeEach
    void setUp() {
        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    @AfterEach
    void tearDown() {
        File folder = new File(UPLOAD_DIR);
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            folder.delete();
        }
    }

//    @Test
//    void processFile_withPdfFile_shouldProcessAndCreateThumbnail() throws Exception {
//        // Arrange
//        String originalFilename = "test document.pdf";
//        String sanitizedFilename = "test_document.pdf";
//        String encodedFilename = java.net.URLEncoder.encode(sanitizedFilename, "UTF-8");
//        byte[] fileBytes = "PDF content".getBytes();
//
//        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
//        when(mockFile.getBytes()).thenReturn(fileBytes);
//
//        try (var documentMock = mockStatic(PDDocument.class);
//             var imageIoMock = mockStatic(ImageIO.class)) {
//            documentMock.when(() -> PDDocument.load(any(File.class))).thenReturn(mockDocument);
//            when(mockDocument.getPages()).thenReturn(mockPageTree);
//            when(mockPageTree.getCount()).thenReturn(1);
//            when(mockPageTree.get(0)).thenReturn(mockPage);
//            when(mockDocument.getNumberOfPages()).thenReturn(1);
//
//            PDFRenderer renderer = new PDFRenderer(mockDocument);
//            try (var rendererMock = mockStatic(PDFRenderer.class, CALLS_REAL_METHODS)) {
//                rendererMock.when(() -> new PDFRenderer(mockDocument).renderImageWithDPI(0, 300))
//                        .thenReturn(mockImage);
//
//                imageIoMock.when(() -> ImageIO.write(any(BufferedImage.class), eq("PNG"), any(File.class)))
//                        .thenReturn(true);
//
//                // Act
//                UploadResult result = uploadService.processFile(mockFile);
//
//                // Assert
//                assertAll(
//                        () -> assertNotNull(result, "Result should not be null"),
//                        () -> assertEquals(UPLOAD_DIR + encodedFilename, result.getOriginalFilePath(), "Original file path should match"),
//                        () -> assertEquals(UPLOAD_DIR + encodedFilename + ".png", result.getThumbnailFilePath(), "Thumbnail file path should match"),
//                        () -> assertTrue(new File(UPLOAD_DIR + encodedFilename).exists(), "Uploaded file should exist")
//                );
//
//                // Verify
//                verify(mockFile).getBytes();
//                verify(mockDocument).close();
//            }
//        }
//    }

    @Test
    void processFile_withNonPdfFile_shouldProcessWithoutThumbnail() throws Exception {
        // Arrange
        String originalFilename = "test.txt";
        String sanitizedFilename = "test.txt";
        String encodedFilename = java.net.URLEncoder.encode(sanitizedFilename, "UTF-8");
        byte[] fileBytes = "Text content".getBytes();

        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        when(mockFile.getBytes()).thenReturn(fileBytes);

        // Act
        UploadResult result = uploadService.processFile(mockFile);

        // Assert
        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(UPLOAD_DIR + encodedFilename, result.getOriginalFilePath(), "File path should match"),
                () -> assertNull(result.getThumbnailFilePath(), "Thumbnail path should be null for non-PDF"),
                () -> assertTrue(new File(UPLOAD_DIR + encodedFilename).exists(), "Uploaded file should exist")
        );

        // Verify
        verify(mockFile).getBytes();
    }

    @Test
    void processFile_withNonPdfFile_shouldThrowException() throws Exception {
        // Arrange
        String originalFilename = "test.txt";

        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            uploadService.processFile(mockFile);
        });

        System.out.println("Exception thrown: " + exception.getMessage());

        assertEquals("Invalid file type. Only PDF files are supported.", exception.getMessage());

        System.out.println("Assertions passed for test: processFile_withNonPdfFile_shouldThrowException");

        // Verify
        verify(mockFile).getOriginalFilename();
    }

    @Test
    void processFile_withIOException_shouldThrowException() throws IOException {
        // Arrange
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getBytes()).thenThrow(new IOException("File read error"));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            uploadService.processFile(mockFile);
        });

        assertEquals("File read error", exception.getMessage());
        verify(mockFile).getBytes();
    }
}