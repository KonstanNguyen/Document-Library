package com.systems.backend.responses;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class HistoryDownloadResponse {
    private String username;
    private String documentName;
    private LocalDateTime date;
    private int totalDownload;
}
