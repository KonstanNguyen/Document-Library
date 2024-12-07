package com.systems.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.systems.backend.responses.HistoryDownloadResponse;
import com.systems.backend.service.HistoryDownloadService;

@RestController
@CrossOrigin
@RequestMapping("/api/history-downloads")
public class HistoryDownloadController {
    @Autowired
    private HistoryDownloadService historyDownloadService;

    @GetMapping
    public ResponseEntity<List<HistoryDownloadResponse>> getAllHistoryDownloads() {
        try {
            List<HistoryDownloadResponse> historyDownloads = historyDownloadService.getAllHistoryDownloads();
            return ResponseEntity.ok(historyDownloads);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
