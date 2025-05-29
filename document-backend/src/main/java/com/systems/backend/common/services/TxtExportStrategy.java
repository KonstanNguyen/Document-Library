package com.systems.backend.common.services;

import jakarta.servlet.http.HttpServletResponse;

import java.beans.Introspector;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

class TxtExportStrategy<T> extends ExportStrategy<T> {

    public TxtExportStrategy(HttpServletResponse response, String fileName) {
        super(response, fileName);
    }

    @Override
    public void export(List<T> data) {
        try {
            response.setContentType("text/plain");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".txt");

            PrintWriter writer = response.getWriter();
            writer.println("Export Report - Generated at: " + LocalDateTime.now());
            writer.println("=======================================");
            for (T r : data) {
                var properties = Introspector.getBeanInfo(r.getClass(), Object.class).getPropertyDescriptors();
                for (var prop : properties) {
                    Object value = prop.getReadMethod().invoke(r);
                    writer.println(prop.getName() + ": " + (value != null ? value.toString() : ""));
                }
                writer.println("---------------------------");
            }
        } catch (Exception e) {
            throw new RuntimeException("TXT export failed", e);
        }
    }
}
