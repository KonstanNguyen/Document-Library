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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.systems.backend.download.mappers.HistoryDownloadMapper;
import com.systems.backend.download.models.HistoryDownload;
import com.systems.backend.download.models.HistoryDownload.HistoryDownloadId;
import com.systems.backend.download.repositories.HistoryDownloadRepository;
import com.systems.backend.download.responses.HistoryDownloadResponse;
import com.systems.backend.download.services.HistoryDownloadService;
import com.systems.backend.users.models.Account;
import com.systems.backend.documents.models.Document;
import com.systems.backend.users.services.AccountService;
import com.systems.backend.common.services.ExportStrategy;
import com.systems.backend.common.services.ExportStrategyFactory;
import com.systems.backend.documents.services.DocumentService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
@RequestMapping("/api/history-downloads")
public class HistoryDownloadController {
    @Autowired
    private HistoryDownloadRepository historyDownloadRepository;
    @Autowired
    private HistoryDownloadService historyDownloadService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DocumentService documentService;

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

    @PostMapping
    public ResponseEntity<String> saveDownloadHistory(
            @RequestParam("accountId") Long accountId,
            @RequestParam("documentId") Long documentId) {
        try {
            Account account = accountService.getAccountById(accountId);
            Document document = documentService.getDocumentById(documentId);

            HistoryDownloadId historyDownloadId = new HistoryDownloadId();
            historyDownloadId.setAccount(account);
            historyDownloadId.setDocument(document);

            HistoryDownload historyDownload = new HistoryDownload();
            historyDownload.setHistoryDownloadId(historyDownloadId);
            historyDownload.setDate(LocalDateTime.now());

            historyDownloadRepository.save(historyDownload);

            return ResponseEntity.ok("Lịch sử tải xuống đã được lưu.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lưu lịch sử tải xuống: " + e.getMessage());
        }
    }

    @GetMapping("{accountId}")
    public ResponseEntity<List<HistoryDownloadResponse>> getHistoryByAccountId(@PathVariable Long accountId) {
        try {
            List<HistoryDownloadResponse> historyDownloads = historyDownloadService.getHistoryByAccountId(accountId);
            return ResponseEntity.ok(historyDownloads);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
