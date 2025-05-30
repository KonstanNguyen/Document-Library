package com.systems.backend.common.services;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

class PdfExportStrategy<T> extends ExportStrategy<T> {
    public PdfExportStrategy(HttpServletResponse response, String fileName) {
        super(response, fileName);
    }

    @Override
    public void export(List<T> data) {
        if (data == null || data.isEmpty()) return;

        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".pdf");

            Document document = new Document(PageSize.A4);
            OutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            // Use bold font for header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            // Extract fields dynamically
            Field[] fields = data.get(0).getClass().getDeclaredFields();

            PdfPTable table = new PdfPTable(fields.length);
            table.setWidthPercentage(100);

            // Add headers
            for (Field field : fields) {
                PdfPCell headerCell = new PdfPCell(new Phrase(field.getName(), headerFont));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(headerCell);
            }

            // Add data rows
            for (T item : data) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(item);
                    PdfPCell dataCell = new PdfPCell(new Phrase(value != null ? value.toString() : "", cellFont));
                    table.addCell(dataCell);
                }
            }

            document.add(table);
            document.close();
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException("PDF export failed", e);
        }
    }
}

