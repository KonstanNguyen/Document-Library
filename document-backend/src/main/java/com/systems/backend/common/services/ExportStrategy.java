package com.systems.backend.common.services;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public abstract class ExportStrategy<T> {
    protected final HttpServletResponse response;
    protected final String fileName;

    protected ExportStrategy(HttpServletResponse response, String fileName) {
        this.response = response;
        this.fileName = fileName;
    }

    public abstract void export(List<T> data);
}
