package com.systems.backend.controller;

import com.systems.backend.upload.domains.UploadController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UploadController.class)
@ExtendWith(SpringExtension.class)
class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String UPLOAD_DIR = "/mock/upload/dir";
    private static final String FILENAME = "testfile";

    @BeforeEach
    void setUp() {
        // Set the upload.dir property for the test
        System.setProperty("upload.dir", UPLOAD_DIR);
    }

    @Test
    void getThumbnail_ShouldReturnImageResource() throws Exception {
        // Mock UrlResource
        UrlResource resource = mock(UrlResource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.isReadable()).thenReturn(true);
        when(resource.getURI()).thenReturn(new URI("file:" + UPLOAD_DIR + "/" + FILENAME));

        // Since UrlResource is created inside the controller, we can't directly mock it here
        // We'll test the happy path assuming the file exists
        mockMvc.perform(get("/api/upload/thumbnail/{filename}", FILENAME))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_PNG_VALUE));
    }

    @Test
    void getThumbnail_WhenFileNotFound_ShouldReturn404() throws Exception {
        // Mock UrlResource for not found scenario
        UrlResource resource = mock(UrlResource.class);
        when(resource.exists()).thenReturn(false);
        when(resource.isReadable()).thenReturn(false);
        when(resource.getURI()).thenReturn(new URI("file:" + UPLOAD_DIR + "/" + FILENAME));

        mockMvc.perform(get("/api/upload/thumbnail/{filename}", FILENAME))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Không tìm thấy file: " + FILENAME));
    }

    @Test
    void getContent_ShouldReturnPdfResource() throws Exception {
        // Mock UrlResource
        UrlResource resource = mock(UrlResource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.isReadable()).thenReturn(true);
        when(resource.getURI()).thenReturn(new URI("file:" + UPLOAD_DIR + "/" + FILENAME));

        mockMvc.perform(get("/api/upload/content/{filename}", FILENAME))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE));
    }

    @Test
    void getContent_WhenFileNotFound_ShouldReturn404() throws Exception {
        // Mock UrlResource for not found scenario
        UrlResource resource = mock(UrlResource.class);
        when(resource.exists()).thenReturn(false);
        when(resource.isReadable()).thenReturn(false);
        when(resource.getURI()).thenReturn(new URI("file:" + UPLOAD_DIR + "/" + FILENAME));

        mockMvc.perform(get("/api/upload/content/{filename}", FILENAME))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Không tìm thấy file: " + FILENAME));
    }
}