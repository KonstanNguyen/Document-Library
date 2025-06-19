package com.systems.backend.download.domains.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systems.backend.download.responses.HistoryDownloadResponse;
import com.systems.backend.download.services.HistoryDownloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HistoryDownloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HistoryDownloadService historyDownloadService;

    private HistoryDownloadResponse historyDownloadResponse;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        historyDownloadResponse = new HistoryDownloadResponse(); // Assuming this has appropriate fields
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void getAllHistoryDownloads_ShouldReturnHistoryDownloads() throws Exception {
        List<HistoryDownloadResponse> historyDownloads = List.of(historyDownloadResponse);
        when(historyDownloadService.getAllHistoryDownloads()).thenReturn(historyDownloads);

        mockMvc.perform(get("/api/history-downloads")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(historyDownloadService).getAllHistoryDownloads();
    }

    @Test
    @WithMockUser(authorities = {"user"})
    void getAllHistoryDownloads_WhenNotAdmin_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/history-downloads")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(historyDownloadService, never()).getAllHistoryDownloads();
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void getAllHistoryDownloads_WhenExceptionThrown_ShouldReturn500() throws Exception {
        when(historyDownloadService.getAllHistoryDownloads()).thenThrow(new RuntimeException("Test exception"));

        mockMvc.perform(get("/api/history-downloads")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(""));

        verify(historyDownloadService).getAllHistoryDownloads();
    }
}