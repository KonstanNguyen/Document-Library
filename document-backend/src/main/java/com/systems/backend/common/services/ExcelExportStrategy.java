package com.systems.backend.common.services;


import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;

class ExcelExportStrategy<T> extends ExportStrategy<T> {

    public ExcelExportStrategy(HttpServletResponse response, String fileName) {
        super(response, fileName);
    }

    private String getFieldValue(T obj, String fieldName) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
            Method getter = pd.getReadMethod();
            Object value = getter.invoke(obj);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void export(List<T> data) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Reports");

            if (!data.isEmpty()) {
                T first = data.get(0);
                var properties = Introspector.getBeanInfo(first.getClass(), Object.class).getPropertyDescriptors();

                XSSFRow headerRow = sheet.createRow(0);
                for (int i = 0; i < properties.length; i++) {
                    headerRow.createCell(i).setCellValue(properties[i].getName());
                }

                int rowIdx = 1;
                for (T r : data) {
                    XSSFRow row = sheet.createRow(rowIdx++);
                    for (int i = 0; i < properties.length; i++) {
                        Object value = properties[i].getReadMethod().invoke(r);
                        row.createCell(i).setCellValue(value != null ? value.toString() : "");
                    }
                }
            }

            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException("Excel export failed", e);
        }
    }
}
