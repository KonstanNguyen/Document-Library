package com.systems.backend.common.services;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.function.BiFunction;

public class ExportStrategyFactory<T> {
    private final Map<String, BiFunction<HttpServletResponse, String, ExportStrategy<T>>> exportStrategies = Map.of(
            "csv", CsvExportStrategy::new,
            "excel", ExcelExportStrategy::new,
            "xlsx", ExcelExportStrategy::new,
            "json", JsonExportStrategy::new,
            "txt", TxtExportStrategy::new,
            "xml", XmlExportStrategy::new,
            "pdf", PdfExportStrategy::new,
            "docx", DocxExportStrategy::new
    );

    public ExportStrategyFactory() {

    }

    public ExportStrategy<T> getExportStrategy(String format, HttpServletResponse response, String fileName) {
        BiFunction<HttpServletResponse, String, ExportStrategy<T>> constructor = exportStrategies.get(format.toLowerCase());
        if (constructor == null) {
            throw new IllegalArgumentException("Unsupported export format: " + format);
        }
        return constructor.apply(response, fileName);
    }
}
