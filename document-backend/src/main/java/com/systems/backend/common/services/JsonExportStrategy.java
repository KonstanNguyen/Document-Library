package com.systems.backend.common.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class JsonExportStrategy<T> extends ExportStrategy<T> {
    public JsonExportStrategy(HttpServletResponse response, String fileName) {
        super(response, fileName);
    }

    @Override
    public void export(List<T> data) {
        try {
            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".json");

            ObjectMapper objectMapper = new JsonMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            Map<String, Object> result = new HashMap<>();
            result.put("title", "Export Report");
            result.put("generatedAt", LocalDateTime.now());
            result.put("data", data);
            objectMapper.writeValue(response.getWriter(), result);
        } catch (IOException e) {
            throw new RuntimeException("JSON export failed", e);
        }
    }
}
