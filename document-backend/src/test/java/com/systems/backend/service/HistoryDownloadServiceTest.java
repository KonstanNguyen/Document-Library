package com.systems.backend.service;

import com.systems.backend.mapper.HistoryDownloadMapper;
import com.systems.backend.model.Account;
import com.systems.backend.model.Document;
import com.systems.backend.model.HistoryDownload;
import com.systems.backend.repository.AccountRepository;
import com.systems.backend.repository.DocumentRepository;
import com.systems.backend.repository.HistoryDownloadRepository;
import com.systems.backend.responses.HistoryDownloadResponse;
import com.systems.backend.service.impl.HistoryDownloadServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryDownloadServiceTest {
    private final Long ACCOUNT_ID = 1L;
    private final Long DOCUMENT_ID = 1L;
    private Account account;
    private Document document;
    private HistoryDownload historyDownload;
    private HistoryDownloadResponse historyDownloadResponse;

    @Mock
    private HistoryDownloadRepository historyDownloadRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private HistoryDownloadMapper historyDownloadMapper;

    @InjectMocks
    private HistoryDownloadServiceImpl historyDownloadService;

    @BeforeEach
    void setUp() {
        account = new Account(ACCOUNT_ID, "testUser", "password", null, null, null, null);
        document = Document.builder()
                .id(DOCUMENT_ID)
                .title("testDoc")
                .build();

        HistoryDownload.HistoryDownloadId historyDownloadId = new HistoryDownload.HistoryDownloadId(account, document);
        historyDownload = new HistoryDownload(historyDownloadId, LocalDateTime.now());

        historyDownloadResponse = new HistoryDownloadResponse(); // Assuming this exists
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllHistoryDownloads() {
        List<HistoryDownload> historyDownloads = List.of(historyDownload);
        when(historyDownloadRepository.findAll()).thenReturn(historyDownloads);
        when(historyDownloadMapper.toDTO(historyDownload)).thenReturn(historyDownloadResponse);

        List<HistoryDownloadResponse> result = historyDownloadService.getAllHistoryDownloads();

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(), "Should contain one history download"),
                () -> assertEquals(historyDownloadResponse, result.get(0), "Response should match mapped DTO")
        );

        verify(historyDownloadRepository).findAll();
        verify(historyDownloadMapper).toDTO(historyDownload);
    }

    @Test
    void getHistoryByAccountId_whenAccountExists_shouldReturnListOfHistory() {
        List<HistoryDownload> historyDownloads = List.of(historyDownload);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(historyDownloadRepository.findByHistoryDownloadId_Account(account)).thenReturn(historyDownloads);
        when(historyDownloadMapper.toDTO(historyDownload)).thenReturn(historyDownloadResponse);

        List<HistoryDownloadResponse> result = historyDownloadService.getHistoryByAccountId(ACCOUNT_ID);

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(), "Should contain one history download"),
                () -> assertEquals(historyDownloadResponse, result.get(0), "Response should match mapped DTO")
        );

        verify(accountRepository).findById(ACCOUNT_ID);
        verify(historyDownloadRepository).findByHistoryDownloadId_Account(account);
        verify(historyDownloadMapper).toDTO(historyDownload);
    }

    @Test
    void getHistoryByAccountId_whenAccountDoesNotExist_shouldThrowResourceNotFoundException() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            historyDownloadService.getHistoryByAccountId(ACCOUNT_ID);
        });

        assertEquals("Account " + ACCOUNT_ID + " not found", thrown.getMessage());

        verify(accountRepository).findById(ACCOUNT_ID);
        verify(historyDownloadRepository, never()).findByHistoryDownloadId_Account(any());
        verify(historyDownloadMapper, never()).toDTO(any());
    }

    @Test
    void getHistoryByDocumentId_whenDocumentExists_shouldReturnListOfHistory() {
        List<HistoryDownload> historyDownloads = List.of(historyDownload);
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        when(historyDownloadRepository.findByHistoryDownloadId_Document(document)).thenReturn(historyDownloads);
        when(historyDownloadMapper.toDTO(historyDownload)).thenReturn(historyDownloadResponse);

        List<HistoryDownloadResponse> result = historyDownloadService.getHistoryByDocumentId(DOCUMENT_ID);

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(), "Should contain one history download"),
                () -> assertEquals(historyDownloadResponse, result.get(0), "Response should match mapped DTO")
        );

        verify(documentRepository).findById(DOCUMENT_ID);
        verify(historyDownloadRepository).findByHistoryDownloadId_Document(document);
        verify(historyDownloadMapper).toDTO(historyDownload);
    }

    @Test
    void getHistoryByDocumentId_whenDocumentDoesNotExist_shouldThrowResourceNotFoundException() {
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            historyDownloadService.getHistoryByDocumentId(DOCUMENT_ID);
        });

        assertEquals("Document " + DOCUMENT_ID + " not found", thrown.getMessage());

        verify(documentRepository).findById(DOCUMENT_ID);
        verify(historyDownloadRepository, never()).findByHistoryDownloadId_Document(any());
        verify(historyDownloadMapper, never()).toDTO(any());
    }
}