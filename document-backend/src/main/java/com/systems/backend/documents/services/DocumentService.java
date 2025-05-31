package com.systems.backend.documents.services;

import com.systems.backend.documents.models.Category;
import com.systems.backend.users.models.DocUser;
import com.systems.backend.documents.models.Document;
import com.systems.backend.documents.resquests.CreateDocumentRequest;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface DocumentService {
    Document getDocumentById(Long id);
    Page<Document> getDocumentByCategory(Category category, Pageable pageable);
    Page<Document> getDocumentsByAuthor(DocUser author, Pageable pageable);
    List<Document> getDocumentsByStatus(Short status);
    List<Document> getDocumentsByCreateAt(LocalDateTime time);
    List<Document> searchDocuments(String keywords);
    Page<Document> getAllDocuments(Pageable pageable);
    Document createDocument(CreateDocumentRequest createDocumentRequest);
    void deleteDocument(Long id);
    Document updateDocument(Long id, Document document);
    List<Document> getDocumentsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
