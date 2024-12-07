package com.systems.backend.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfPageExtractor {
    public static byte[] extractPage(String pdfPath, int pageNumber) throws IOException {
        try (PDDocument document = PDDocument.load(new java.io.File(pdfPath))) {
            if (pageNumber < 1 || pageNumber > document.getNumberOfPages()) {
                throw new IllegalArgumentException("Invalid page number");
            }

            PDDocument allPagesDocument  = new PDDocument();
            for (int i = 0; i < pageNumber; i++) {
                PDPage page = document.getPage(i);  // Lấy từng trang từ document gốc
                allPagesDocument.addPage(page);     // Thêm trang vào document mới
            }
    
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            allPagesDocument.save(baos);  // Lưu tài liệu vào ByteArrayOutputStream
            allPagesDocument.close();

            return baos.toByteArray();
        }
    }
}
