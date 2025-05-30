package com.systems.backend.common.services;

import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

class CsvExportStrategy<T> extends ExportStrategy<T> {

    public CsvExportStrategy(HttpServletResponse response, String fileName) {
        super(response, fileName);
    }

    @Override
    public void export(List<T> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".csv");

            ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

            // Extract field names dynamically from the first object
            T sample = data.get(0);
            Field[] fields = sample.getClass().getDeclaredFields();
            String[] fieldNames = Arrays.stream(fields).map(Field::getName).toArray(String[]::new);

            csvWriter.writeHeader(fieldNames); // use field names as headers

            for (T item : data) {
                csvWriter.write(item, fieldNames);
            }

            csvWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("CSV export failed", e);
        }
    }
}
