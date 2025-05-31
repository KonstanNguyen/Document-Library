package com.systems.backend.download.domains.common;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.systems.backend.download.responses.HistoryDownloadResponse;
import com.systems.backend.download.services.HistoryDownloadService;
import com.systems.backend.common.services.ExportStrategy;
import com.systems.backend.common.services.ExportStrategyFactory;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
@RequestMapping("/api/history-downloads")
public class HistoryDownloadController {
    @Autowired
    private HistoryDownloadService historyDownloadService;

    @PreAuthorize("hasAnyAuthority('admin') or hasAnyAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<HistoryDownloadResponse>> getAllHistoryDownloads() {
        try {
            List<HistoryDownloadResponse> historyDownloads = historyDownloadService.getAllHistoryDownloads();
            return ResponseEntity.ok(historyDownloads);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('admin') or hasAnyAuthority('ADMIN')")
    @GetMapping("/export/{format}")
    @ResponseStatus(HttpStatus.OK)
    public void exportHistoryDownloads(
            @PathVariable String format,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response) {
        String exportFileName = fileName != null ? fileName : "history-downloads";
        List<HistoryDownloadResponse> historyDownloads;

        if (startDate != null && endDate != null) {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            historyDownloads = historyDownloadService.getHistoryByDateRange(start, end);
        } else {
            historyDownloads = historyDownloadService.getAllHistoryDownloads();
        }

        ExportStrategyFactory<HistoryDownloadResponse> factory = new ExportStrategyFactory<>();
        ExportStrategy<HistoryDownloadResponse> strategy = factory.getExportStrategy(format, response, exportFileName);
        strategy.export(historyDownloads);
    }
}
