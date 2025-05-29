package com.systems.backend.common.services;


import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xwpf.usermodel.*;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;

class DocxExportStrategy<T> extends ExportStrategy<T> {
    public DocxExportStrategy(HttpServletResponse response, String fileName) {
        super(response, fileName);
    }

    @Override
    public void export(List<T> data) {
        try {
            // Set response headers for DOCX
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".docx");

            // Create a new Word document
            XWPFDocument document = new XWPFDocument();

            if (!data.isEmpty()) {
                T first = data.getFirst();
                PropertyDescriptor[] properties = Introspector.getBeanInfo(first.getClass(), Object.class)
                        .getPropertyDescriptors();

                // Create a table with the same number of columns as properties
                XWPFTable table = document.createTable();

                // Add header row
                XWPFTableRow headerRow = table.getRow(0);
                for (int i = 0; i < properties.length; i++) {
                    if (i > 0) {
                        headerRow.createCell(); // Create additional cells for headers
                    }
                    headerRow.getCell(i).setText(properties[i].getName());
                }

                // Add data rows
                for (T r : data) {
                    XWPFTableRow row = table.createRow();
                    for (int i = 0; i < properties.length; i++) {
                        Object value = properties[i].getReadMethod().invoke(r);
                        row.getCell(i).setText(value != null ? value.toString() : "");
                    }
                }
            } else {
                // Add "No data available" paragraph
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText("No data available.");
            }

            // Write the document to the response output stream
            document.write(response.getOutputStream());
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("DOCX export failed", e);
        }
    }
}