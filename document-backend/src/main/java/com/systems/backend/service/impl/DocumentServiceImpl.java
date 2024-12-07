package com.systems.backend.service.impl;

import com.systems.backend.model.Category;
import com.systems.backend.model.DocUser;
import com.systems.backend.model.Document;
import com.systems.backend.repository.CategoryRepository;
import com.systems.backend.repository.DocUserRepository;
import com.systems.backend.repository.DocumentRepository;
import com.systems.backend.requests.CreateDocumentRequest;
import com.systems.backend.service.DocumentService;
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
    private DocUserRepository docUserRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Document getDocumentById(Long id) {
        Optional<Document> documentOptional = documentRepository.findById(id);
        return documentOptional.orElse(null);
    }

    @Override
    public Page<Document> gettDocumentByCategory(Category category, Pageable pageable) {
        return documentRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Document> getDocumentsByAuthor(DocUser author, Pageable pageable) {
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
            throw new RuntimeException("This document has already existed");
        }

        DocUser author = docUserRepository.findById(createDocumentRequest.getAuthorId()).orElseThrow(() -> new ResourceNotFoundException("Not found user by ID" + createDocumentRequest.getAuthorId())) ;
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
            throw new RuntimeException("Document is not found!");
        }
        documentRepository.delete(checkDocument);
    }
    
    @Override
    public Document updateDocument(Long id, Document document) {
        Document updatedDocument = getDocumentById(id);
        if (updatedDocument == null) {
            throw new RuntimeException("This document is not found");
        }


        updatedDocument.setAuthor(Optional.ofNullable(document.getAuthor()).orElse(updatedDocument.getAuthor()));
        updatedDocument.setCategory(Optional.ofNullable(document.getCategory()).orElse(updatedDocument.getCategory()));
        updatedDocument.setContent(Optional.ofNullable(document.getContent()).orElse(updatedDocument.getContent()));
        updatedDocument.setStatus(Optional.ofNullable(document.getStatus()).orElse(updatedDocument.getStatus()));
        updatedDocument.setThumbnail(Optional.ofNullable(document.getThumbnail()).orElse(updatedDocument.getThumbnail()));
        updatedDocument.setTitle(Optional.ofNullable(document.getTitle()).orElse(updatedDocument.getTitle()));
        updatedDocument.setUpdateAt(LocalDateTime.now());

        return documentRepository.save(updatedDocument);
        
    }
}
