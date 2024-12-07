package com.systems.backend.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCategoryRequest {
    @NotBlank(message = "categoryId is required!")
    private Long categoryId;
    private String description;
    @NotBlank(message = "Name is required!")
    private String name;
}
