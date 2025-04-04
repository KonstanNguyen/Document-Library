package com.systems.backend.controller;

import com.systems.backend.utils.PdfPageExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PdfController.class)
@ExtendWith(SpringExtension.class)
class PdfControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads";
    private static final String PDF_PATH = "test.pdf";
    private static final int PAGE_NUMBER = 1;

    @BeforeEach
    void setUp() {
        // No additional setup needed for this controller as it doesn't use @Autowired dependencies
    }

    @Test
    void getPdfPage_ShouldReturnPdfPage() throws Exception {
        byte[] pdfPageContent = "PDF page content".getBytes();

        // Mock the static method using try-with-resources to ensure the mock is closed
        try (MockedStatic<PdfPageExtractor> mockedStatic = mockStatic(PdfPageExtractor.class)) {
            mockedStatic.when(() -> PdfPageExtractor.extractPage(PDF_PATH, PAGE_NUMBER))
                    .thenReturn(pdfPageContent);

            mockMvc.perform(get("/pdf/page")
                            .param("pdfPath", PDF_PATH)
                            .param("pageNumber", String.valueOf(PAGE_NUMBER))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "inline; filename=page-1.pdf"))
                    .andExpect(header().string("Content-Type", "application/pdf"))
                    .andExpect(content().bytes(pdfPageContent));

            mockedStatic.verify(() -> PdfPageExtractor.extractPage(PDF_PATH, PAGE_NUMBER), times(1));
        }
    }

    @Test
    void getPdfPage_WhenExceptionThrown_ShouldReturn400() throws Exception {
        // Mock the static method to throw an exception
        try (MockedStatic<PdfPageExtractor> mockedStatic = mockStatic(PdfPageExtractor.class)) {
            mockedStatic.when(() -> PdfPageExtractor.extractPage(PDF_PATH, PAGE_NUMBER))
                    .thenThrow(new RuntimeException("Extraction failed"));

            mockMvc.perform(get("/pdf/page")
                            .param("pdfPath", PDF_PATH)
                            .param("pageNumber", String.valueOf(PAGE_NUMBER))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            mockedStatic.verify(() -> PdfPageExtractor.extractPage(PDF_PATH, PAGE_NUMBER), times(1));
        }
    }

    @Test
    void downloadPdf_ShouldReturnPdfFile() throws Exception {
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.getName()).thenReturn(PDF_PATH);

        // Since FileSystemResource is created inside the controller, we can't mock it directly
        // Test assumes file exists in the mock environment
        mockMvc.perform(get("/pdf/download")
                        .param("pdfPath", PDF_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=" + PDF_PATH))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE));
    }

    @Test
    void downloadPdf_WhenFileNotFound_ShouldReturn404() throws Exception {
        String nonExistentPath = "nonexistent.pdf";

        mockMvc.perform(get("/pdf/download")
                        .param("pdfPath", nonExistentPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}