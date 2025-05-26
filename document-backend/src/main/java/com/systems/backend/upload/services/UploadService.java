package com.systems.backend.upload.services;

import org.springframework.web.multipart.MultipartFile;

import com.systems.backend.common.utils.UploadResult;

import org.springframework.stereotype.Service;

@Service
public interface UploadService {
    UploadResult  processFile(MultipartFile file) throws Exception;
}
