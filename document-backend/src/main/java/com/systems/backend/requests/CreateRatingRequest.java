package com.systems.backend.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRatingRequest {
    private Long accountId;
    private Long documentId;
    @Min(value = 1, message = "Rate must be greater than 0")
    @Max(value = 5, message = "Rate must be less than 5")
    private Short rate;
}
