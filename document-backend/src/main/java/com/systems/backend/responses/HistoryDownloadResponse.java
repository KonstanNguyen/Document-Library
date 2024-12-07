package com.systems.backend.responses;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class HistoryDownloadResponse {
    private Long accountId;
    private Long documentId;
    private LocalDateTime date;
}
