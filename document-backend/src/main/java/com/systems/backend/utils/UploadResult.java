package com.systems.backend.utils;

public class UploadResult {
    private String originalFilePath;
    private String thumbnailFilePath;

    public UploadResult(String originalFilePath, String thumbnailFilePath) {
        this.originalFilePath = originalFilePath;
        this.thumbnailFilePath = thumbnailFilePath;
    }

    public String getOriginalFilePath() {
        return originalFilePath;
    }

    public void setOriginalFilePath(String originalFilePath) {
        this.originalFilePath = originalFilePath;
    }

    public String getThumbnailFilePath() {
        return thumbnailFilePath;
    }

    public void setThumbnailFilePath(String thumbnailFilePath) {
        this.thumbnailFilePath = thumbnailFilePath;
    }
}
