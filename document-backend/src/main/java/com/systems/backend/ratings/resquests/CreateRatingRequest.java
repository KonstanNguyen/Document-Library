package com.systems.backend.ratings.resquests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRatingRequest {
    private Long accountId;
    private Long documentId;
    @Min(value = 1, message = "Rate must be greater than 0")
    @Max(value = 5, message = "Rate must be less than 5")
    private int rate;
}
