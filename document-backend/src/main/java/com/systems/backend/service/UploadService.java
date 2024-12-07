package com.systems.backend.service;

import org.springframework.web.multipart.MultipartFile;

import com.systems.backend.utils.UploadResult;

import org.springframework.stereotype.Service;

@Service
public interface UploadService {
    UploadResult  processFile(MultipartFile file) throws Exception;
}
