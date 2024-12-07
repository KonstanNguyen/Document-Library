package com.systems.backend.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.systems.backend.responses.HistoryDownloadResponse;

@Service
public interface HistoryDownloadService {
    
    List<HistoryDownloadResponse> getAllHistoryDownloads();

    List<HistoryDownloadResponse> getHistoryByAccountId(Long accountId);

    List<HistoryDownloadResponse> getHistoryByDocumentId(Long documentId);
}
