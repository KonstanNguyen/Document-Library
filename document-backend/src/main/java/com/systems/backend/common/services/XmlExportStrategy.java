package com.systems.backend.common.services;


import jakarta.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.lang.reflect.Field;
import java.util.List;

class XmlExportStrategy<T> extends ExportStrategy<T> {

    public XmlExportStrategy(HttpServletResponse response, String fileName) {
        super(response, fileName);
    }

    @Override
    public void export(List<T> data) {
        if (data == null || data.isEmpty()) return;

        try {
            response.setContentType("application/xml");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Root element
            Element rootElement = doc.createElement(data.getFirst().getClass().getSimpleName() + "s");
            doc.appendChild(rootElement);
            Field[] fields = data.getFirst().getClass().getDeclaredFields();
            for (T item : data) {
                Element itemElement = doc.createElement(item.getClass().getSimpleName());

                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(item);

                    Element fieldElement = doc.createElement(field.getName());
                    fieldElement.appendChild(doc.createTextNode(value != null ? value.toString() : ""));
                    itemElement.appendChild(fieldElement);
                }

                rootElement.appendChild(itemElement);
            }

            // Write XML to response
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(response.getOutputStream());

            transformer.transform(source, result);
        } catch (Exception e) {
            throw new RuntimeException("XML export failed", e);
        }
    }
}
