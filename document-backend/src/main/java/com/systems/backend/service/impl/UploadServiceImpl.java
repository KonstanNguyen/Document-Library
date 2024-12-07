package com.systems.backend.service.impl;

import com.systems.backend.service.UploadService;
import com.systems.backend.utils.UploadResult;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.web.multipart.MultipartFile;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

@Service
public class UploadServiceImpl implements UploadService {
    @Override
    public UploadResult processFile(MultipartFile file) throws Exception {
        String uploadDir = "uploads/";
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String sanitizedFilename = originalFilename.replaceAll("\\s+", "_"); // Thay khoảng trắng bằng _
        String encodedFilename = URLEncoder.encode(sanitizedFilename, StandardCharsets.UTF_8.toString());
        String filePath = uploadDir + encodedFilename;

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(file.getBytes());
        }
        if (originalFilename.endsWith(".pdf")) {
            File pdfFile = new File(filePath);
            PDDocument document = PDDocument.load(pdfFile);
            PDFRenderer renderer = new PDFRenderer(document);

            BufferedImage image = renderer.renderImageWithDPI(0, 300); // Render trang đầu tiên
            String thumbnailPath = uploadDir + encodedFilename + ".png";
            ImageIO.write(image, "PNG", new File(thumbnailPath));

            document.close();

            return new UploadResult(filePath, thumbnailPath);
        } else {
            return new UploadResult(filePath, null);
        }
    }
}
