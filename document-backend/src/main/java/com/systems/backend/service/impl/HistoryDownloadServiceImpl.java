package com.systems.backend.service.impl;

import com.systems.backend.mapper.HistoryDownloadMapper;
import com.systems.backend.model.Account;
import com.systems.backend.model.Document;
import com.systems.backend.model.HistoryDownload;
import com.systems.backend.repository.AccountRepository;
import com.systems.backend.repository.DocumentRepository;
import com.systems.backend.repository.HistoryDownloadRepository;
import com.systems.backend.responses.HistoryDownloadResponse;
import com.systems.backend.service.HistoryDownloadService;

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
}
