package com.systems.backend.documents.services.impl;

import com.systems.backend.documents.models.Category;
import com.systems.backend.users.models.User;
import com.systems.backend.documents.models.Document;
import com.systems.backend.documents.repositories.CategoryRepository;
import com.systems.backend.users.repositories.UserRepository;
import com.systems.backend.documents.repositories.DocumentRepository;
import com.systems.backend.documents.resquests.CreateDocumentRequest;
import com.systems.backend.documents.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Document getDocumentById(Long id) {
        Optional<Document> documentOptional = documentRepository.findById(id);
        return documentOptional.orElseThrow(() ->
                new ResourceNotFoundException("Document is not found!")
        );
    }

    @Override
    public Page<Document> getDocumentByCategory(Category category, Pageable pageable) {
        return documentRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Document> getDocumentsByAuthor(User author, Pageable pageable) {
        return documentRepository.findByAuthor(author, pageable);
    }

    @Override
    public List<Document> getDocumentsByStatus(Short status) {
        return documentRepository.findByStatus(status);
    }

    @Override
    public List<Document> getDocumentsByCreateAt(LocalDateTime time) {
        return documentRepository.findByCreateAt(time);
    }

    @Override
    public List<Document> searchDocuments(String keywords) {
        return documentRepository.findByTitleContaining(keywords);
    }

    @Override
    public Page<Document> getAllDocuments(Pageable pageable) {
        return documentRepository.findAll(pageable);
    }

    @Override
    public Document createDocument(CreateDocumentRequest createDocumentRequest) {
        if (documentRepository.existsByTitle(createDocumentRequest.getTitle())) {
            throw new IllegalStateException("This document has already existed");
        }

        User author = userRepository.findById(createDocumentRequest.getAuthorId()).orElseThrow(() -> new ResourceNotFoundException("Not found user by ID" + createDocumentRequest.getAuthorId()));
        Category category = categoryRepository.findById(createDocumentRequest.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Not found category by ID" + createDocumentRequest.getCategoryId()));

        Document document = Document.builder()
                .status(createDocumentRequest.getStatus())
                .title(createDocumentRequest.getTitle())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .author(author)
                .category(category)
                .thumbnail(createDocumentRequest.getThumbnail())
                .content(createDocumentRequest.getContent())
                .description(createDocumentRequest.getDescription())
                .build();
        return documentRepository.save(document);
    }

    @Override
    public void deleteDocument(Long id) {
        Document checkDocument = getDocumentById(id);
        if (checkDocument == null) {
            throw new ResourceNotFoundException("Document is not found!");
        }
        documentRepository.delete(checkDocument);
    }

    @Override
    public Document updateDocument(Long id, Document document) {
        Document updatedDocument = getDocumentById(id);
        if (updatedDocument == null) {
            throw new ResourceNotFoundException("This document is not found");
        }

        Category category = document.getCategory();
        if (category != null && category.getId() != null) {
            category = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            updatedDocument.setCategory(category);
        }

        updatedDocument.setAuthor(Optional.ofNullable(document.getAuthor()).orElse(updatedDocument.getAuthor()));
        updatedDocument.setContent(Optional.ofNullable(document.getContent()).orElse(updatedDocument.getContent()));
        updatedDocument.setStatus(Optional.ofNullable(document.getStatus()).orElse(updatedDocument.getStatus()));
        updatedDocument.setThumbnail(Optional.ofNullable(document.getThumbnail()).orElse(updatedDocument.getThumbnail()));
        updatedDocument.setTitle(Optional.ofNullable(document.getTitle()).orElse(updatedDocument.getTitle()));
        updatedDocument.setDescription(Optional.ofNullable(document.getDescription()).orElse(updatedDocument.getDescription()));
        updatedDocument.setUpdateAt(LocalDateTime.now());

        return documentRepository.save(updatedDocument);

    }

    @Override
    public Document updateDocumentFromRequest(Long id, CreateDocumentRequest request) {
        Document existingDocument = getDocumentById(id);
        if (existingDocument == null) {
            throw new ResourceNotFoundException("Document not found");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        Document document = Document.builder()
                .id(id)
                .title(request.getTitle())
                .category(category != null ? category : existingDocument.getCategory())
                .author(existingDocument.getAuthor())
                .content(request.getContent())
                .thumbnail(request.getThumbnail())
                .description(request.getDescription())
                .status(request.getStatus())
                .views(existingDocument.getViews())
                .createAt(existingDocument.getCreateAt())
                .updateAt(LocalDateTime.now())
                .build();

        return updateDocument(id, document);
    }

    @Override
    public List<Document> getDocumentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return documentRepository.findByCreateAtBetween(startDate, endDate);
    }
}
