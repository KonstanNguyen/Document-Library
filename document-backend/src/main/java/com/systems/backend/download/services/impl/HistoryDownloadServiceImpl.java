package com.systems.backend.download.services.impl;

import com.systems.backend.download.mappers.HistoryDownloadMapper;
import com.systems.backend.users.models.Account;
import com.systems.backend.documents.models.Document;
import com.systems.backend.download.models.HistoryDownload;
import com.systems.backend.users.repositories.AccountRepository;
import com.systems.backend.documents.repositories.DocumentRepository;
import com.systems.backend.download.repositories.HistoryDownloadRepository;
import com.systems.backend.download.responses.HistoryDownloadResponse;
import com.systems.backend.download.services.HistoryDownloadService;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class HistoryDownloadServiceImpl implements HistoryDownloadService {
    @Autowired
    private HistoryDownloadRepository historyDownloadRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private HistoryDownloadMapper historyDownloadMapper;

    @Override
    public List<HistoryDownloadResponse> getAllHistoryDownloads() {
        return historyDownloadRepository.findAll().stream().map(
            historyDownload -> historyDownloadMapper.toDTO(historyDownload)
        ).toList();
    }

    @Override
    public List<HistoryDownloadResponse> getHistoryByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new ResourceNotFoundException("Account " + accountId + " not found")
        );
        List<HistoryDownload> historyDownloads = historyDownloadRepository.findByHistoryDownloadId_Account(account);

        return historyDownloads.stream().map(
            historyDownload -> historyDownloadMapper.toDTO(historyDownload)
        ).toList();
    }

    @Override
    public List<HistoryDownloadResponse> getHistoryByDocumentId(Long documentId) {
        Document document = documentRepository.findById(documentId).orElseThrow(() ->
                new ResourceNotFoundException("Document " + documentId + " not found")
        );

        List<HistoryDownload> historyDownloads = historyDownloadRepository.findByHistoryDownloadId_Document(document);

        return historyDownloads.stream().map(
            historyDownload -> historyDownloadMapper.toDTO(historyDownload)
        ).toList();
    }

    @Override
    public List<HistoryDownloadResponse> getHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<HistoryDownload> historyDownloads = historyDownloadRepository.findByDateBetween(startDate, endDate);
        return historyDownloads.stream()
                .map(historyDownload -> historyDownloadMapper.toDTO(historyDownload))
                .toList();
    }
}
