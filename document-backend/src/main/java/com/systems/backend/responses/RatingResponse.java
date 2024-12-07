package com.systems.backend.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingResponse {
    private Long accountId;
    private Long documentId;
    private Short rate;
}
