package com.systems.backend.requests;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDocumentRequest {
    @NotBlank(message = "Status is required!")
    private Short status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    @NotBlank(message = "Thumbnail is required!")
    private String thumbnail;
    @NotBlank(message = "Title is required!")
    private String title;
    @NotBlank(message = "Author is required!")
    private Long authorId;
    @NotBlank(message = "Category is required!")
    private Long categoryId;
    @NotBlank(message = "Content is required!")
    private String content;
    private String description;
}