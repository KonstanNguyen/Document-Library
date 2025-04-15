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

        System.out.println("Number of history downloads retrieved: " + result.size());

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(),
                        () -> "Expected 1 history download, but got: " + result.size()),
                () -> assertEquals(historyDownloadResponse, result.get(0),
                        () -> "Expected response does not match actual response.")
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

        System.out.println("Number of history downloads for account " + ACCOUNT_ID + ": " + result.size());
        result.forEach(res -> System.out.println(" - HistoryDownloadResponse: " + res));

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(),
                        () -> "Expected 1 history download, but got: " + result.size()),
                () -> assertEquals(historyDownloadResponse, result.get(0),
                        () -> "Expected response does not match actual response.")
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

        System.out.println("Exception thrown: " + thrown.getMessage());

        assertEquals("Account " + ACCOUNT_ID + " not found", thrown.getMessage(),
                () -> "Expected exception message does not match actual message.");

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

        System.out.println("Number of history downloads for document " + DOCUMENT_ID + ": " + result.size());
        result.forEach(res -> System.out.println(" - HistoryDownloadResponse: " + res));

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(),
                        () -> "Expected 1 history download, but got: " + result.size()),
                () -> assertEquals(historyDownloadResponse, result.get(0),
                        () -> "Expected response does not match actual response.")
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

        System.out.println("Exception thrown: " + thrown.getMessage());

        assertEquals("Document " + DOCUMENT_ID + " not found", thrown.getMessage(),
                () -> "Expected exception message does not match actual message.");

        verify(documentRepository).findById(DOCUMENT_ID);
        verify(historyDownloadRepository, never()).findByHistoryDownloadId_Document(any());
        verify(historyDownloadMapper, never()).toDTO(any());
    }
}