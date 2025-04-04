package com.systems.backend.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDownloadResponse {
    private String username;
    private String documentName;
    private LocalDateTime date;
    private int totalDownload;
}
