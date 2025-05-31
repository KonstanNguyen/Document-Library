package com.systems.backend.download.services;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.systems.backend.download.responses.HistoryDownloadResponse;

@Service
public interface HistoryDownloadService {
    
    List<HistoryDownloadResponse> getAllHistoryDownloads();

    List<HistoryDownloadResponse> getHistoryByAccountId(Long accountId);

    List<HistoryDownloadResponse> getHistoryByDocumentId(Long documentId);

    List<HistoryDownloadResponse> getHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
