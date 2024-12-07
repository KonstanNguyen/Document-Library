package com.systems.backend.responses;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponse {
    private Long id;
    private Long categoryId;
    private Long authorId;
    private Short status;
    private String thumbnail;
    private String title;
    private String content;
    private String description;
    private float ratingAvg;
    private int views;
    @JsonFormat(pattern="HH:mm:ss dd-MM-yyyy")
    private LocalDateTime createAt;
    @JsonFormat(pattern="HH:mm:ss dd-MM-yyyy")
    private LocalDateTime updateAt;
}
