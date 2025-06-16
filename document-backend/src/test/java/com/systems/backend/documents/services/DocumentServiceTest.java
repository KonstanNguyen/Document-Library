package com.systems.backend.documents.services;

import com.systems.backend.documents.models.Category;
import com.systems.backend.documents.models.Document;
import com.systems.backend.documents.repositories.CategoryRepository;
import com.systems.backend.documents.repositories.DocumentRepository;
import com.systems.backend.documents.resquests.CreateDocumentRequest;
import com.systems.backend.documents.services.impl.DocumentServiceImpl;
import com.systems.backend.users.models.User;
import com.systems.backend.users.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {
    private final long DOCUMENT_ID = 1L;
    private final String DOCUMENT_TITLE = "Document";
    private List<Document> documents;
    private Document document;
    private Category category;
    private User author;

    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private UserRepository docUserRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @BeforeEach
    void setUp() {
        category = new Category(1L, null, "category1", null);
        author = User.builder()
                .id(1L)
                .name("ntn")
                .gender(true)
                .email("email")
                .build();

        document = Document.builder()
                .id(1L)
                .title("title1")
                .thumbnail("thumbnail1")
                .content("content1")
                .description("description1")
                .category(category)
                .author(author)
                .status((short) 1)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        documents = List.of(
                Document.builder()
                        .id(1L)
                        .title("title1")
                        .thumbnail("thumbnail1")
                        .content("content1")
                        .description("description1")
                        .build(),
                Document.builder()
                        .id(2L)
                        .title("title2")
                        .thumbnail("thumbnail2")
                        .content("content2")
                        .description("description2")
                        .build(),
                Document.builder()
                        .id(3L)
                        .title("title3")
                        .thumbnail("thumbnail3")
                        .content("content3")
                        .description("description3")
                        .build());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getDocumentById_whenDocumentExists_shouldReturnDocument() {
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));

        Document result = documentService.getDocumentById(DOCUMENT_ID);

        System.out.println("Document retrieved: " + result.getTitle());

        assertAll(
                () -> assertNotNull(result, "Document should not be null"),
                () -> assertEquals("title1", result.getTitle(), "Title should match"));

        verify(documentRepository).findById(DOCUMENT_ID);
    }

    @Test
    void getDocumentById_whenDocumentDoesNotExist_shouldThrowResourceNotFoundException() {
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            documentService.getDocumentById(DOCUMENT_ID);
        });

        System.out.println(thrown.getMessage());

        assertEquals("Document is not found!", thrown.getMessage());
        verify(documentRepository).findById(DOCUMENT_ID);
    }

    @Test
    void getDocumentByCategory_whenDocumentExists_shouldReturnDocument() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Document> page = new PageImpl<>(List.of(document));
        when(documentRepository.findByCategory(category, pageable)).thenReturn(page);

        Page<Document> result = documentService.getDocumentByCategory(category, pageable);

        System.out.println("Number of documents retrieved: " + result.getContent().size());
        result.getContent().forEach(doc -> System.out.println(" - Document: " + doc.getTitle()));

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.getContent().size(), "Should contain one document"),
                () -> assertEquals("title1", result.getContent().get(0).getTitle(),
                        "Title should match"));

        verify(documentRepository).findByCategory(category, pageable);
    }

    @Test
    void getDocumentByCategory_whenDocumentDoesNotExists_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Document> emptyPage = new PageImpl<>(List.of());
        when(documentRepository.findByCategory(category, pageable)).thenReturn(emptyPage);

        Page<Document> result = documentService.getDocumentByCategory(category, pageable);

        System.out.println("Number of documents retrieved: " + result.getContent().size());

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(0, result.getContent().size(), "Should be empty"));

        verify(documentRepository).findByCategory(category, pageable);
    }

    @Test
    void getDocumentsByAuthor_whenDocumentExists_shouldReturnDocument() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Document> page = new PageImpl<>(List.of(document));
        when(documentRepository.findByAuthor(author, pageable)).thenReturn(page);

        Page<Document> result = documentService.getDocumentsByAuthor(author, pageable);

        System.out.println("Number of documents retrieved: " + result.getContent().size());
        result.getContent().forEach(doc -> System.out.println(" - Document: " + doc.getTitle()));

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.getContent().size(), "Should contain one document"));

        verify(documentRepository).findByAuthor(author, pageable);
    }

    @Test
    void getDocumentsByAuthor_whenDocumentDoesNotExists_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Document> emptyPage = new PageImpl<>(List.of());
        when(documentRepository.findByAuthor(author, pageable)).thenReturn(emptyPage);

        Page<Document> result = documentService.getDocumentsByAuthor(author, pageable);

        System.out.println("Number of documents retrieved: " + result.getContent().size());

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(0, result.getContent().size(), "Should be empty"));

        verify(documentRepository).findByAuthor(author, pageable);
    }

    @Test
    void getDocumentsByStatus_whenDocumentExists_shouldReturnDocument() {
        Short status = 1;
        when(documentRepository.findByStatus(status)).thenReturn(List.of(document));

        List<Document> result = documentService.getDocumentsByStatus(status);

        System.out.println("Number of documents retrieved: " + result.size());
        result.forEach(doc -> System.out.println(" - Document: " + doc.getTitle()));

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(), "Should contain one document"));

        verify(documentRepository).findByStatus(status);
    }

    @Test
    void getDocumentsByStatus_whenDocumentDoesNotExists_shouldReturnEmptyList() {
        Short status = 1;
        when(documentRepository.findByStatus(status)).thenReturn(List.of());

        List<Document> result = documentService.getDocumentsByStatus(status);

        System.out.println("Number of documents retrieved: " + result.size());

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(0, result.size(), "Should be empty"));

        verify(documentRepository).findByStatus(status);
    }

    @Test
    void getDocumentsByCreateAt_whenDocumentExists_shouldReturnDocument() {
        LocalDateTime time = LocalDateTime.now();
        when(documentRepository.findByCreateAt(time)).thenReturn(List.of(document));

        List<Document> result = documentService.getDocumentsByCreateAt(time);

        System.out.println("Number of documents retrieved: " + result.size());
        result.forEach(doc -> System.out.println(" - Document: " + doc.getTitle()));

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(), "Should contain one document"));

        verify(documentRepository).findByCreateAt(time);
    }

    @Test
    void getDocumentsByCreateAt_whenDocumentDoesNotExists_shouldReturnEmptyList() {
        LocalDateTime time = LocalDateTime.now();
        when(documentRepository.findByCreateAt(time)).thenReturn(List.of());

        List<Document> result = documentService.getDocumentsByCreateAt(time);

        System.out.println("Number of documents retrieved: " + result.size());

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(0, result.size(), "Should be empty"));

        verify(documentRepository).findByCreateAt(time);
    }

    @Test
    void getAllDocuments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Document> page = new PageImpl<>(documents);
        when(documentRepository.findAll(pageable)).thenReturn(page);

        Page<Document> result = documentService.getAllDocuments(pageable);

        System.out.println("Number of documents retrieved: " + result.getContent().size());
        result.getContent().forEach(doc -> System.out.println(" - Document: " + doc.getTitle()));

        assertAll(
                () -> assertNotNull(result, "The result should not be null"),
                () -> assertEquals(3, result.getContent().size(),
                        "The document list should contain exactly 3 items"));

        verify(documentRepository).findAll(pageable);
    }

    @Test
    void createDocument() {
        CreateDocumentRequest request = new CreateDocumentRequest(
                1L, 1L, "title1",
                (short) 1, "thumbnail1", "content1", "description1",
                LocalDateTime.now(), LocalDateTime.now());

        when(documentRepository.existsByTitle("title1")).thenReturn(false);
        when(docUserRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        Document result = documentService.createDocument(request);

        System.out.println("Document created: " + result.getTitle() + "\n"
                + "Document ID: " + result.getId() + "\n"
                + "Document Author: " + result.getAuthor().getName() + "\n"
                + "Document Category: " + result.getCategory().getName());
        System.out.println("Document created successfully!");

        assertAll(
                () -> assertNotNull(result, "The returned Document should not be null"),
                () -> assertEquals("title1", result.getTitle(),
                        "The title of the created document should be 'title1'"));

        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void createDocument_whenTitleExists_shouldThrowIllegalStateException() {
        CreateDocumentRequest request = new CreateDocumentRequest(1L, 1L,
                "title1", (short) 1, "thumbnail1", "content1",
                "description1", LocalDateTime.now(), LocalDateTime.now());
        when(documentRepository.existsByTitle("title1")).thenReturn(true);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            documentService.createDocument(request);
        });

        System.out.println(thrown.getMessage());

        assertEquals("This document has already existed", thrown.getMessage());
    }

    @Test
    void deleteDocument() {
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        doNothing().when(documentRepository).delete(document);

        documentService.deleteDocument(DOCUMENT_ID);

        System.out.println("\n\n\nDocument deleted successfully: " + DOCUMENT_ID);

        verify(documentRepository).findById(DOCUMENT_ID);
        verify(documentRepository).delete(document);
    }

    @Test
    void updateDocument() {
        Document updates = Document.builder()
                .title("updatedTitle")
                .content("updatedContent")
                .build();
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        Document result = documentService.updateDocument(DOCUMENT_ID, updates);

        System.out.println("Document updated successfully!: " + result.getTitle() +
                ", Content: " + result.getContent());

        assertAll(
                () -> assertNotNull(result, () -> "Updated document is null. Expected a non-null document."),
                () -> assertEquals("updatedTitle", result.getTitle(),
                        () -> "Title mismatch. Expected: 'updatedTitle', Actual: '" + result.getTitle() + "'"),
                () -> assertEquals("updatedContent", result.getContent(),
                        () -> "Content mismatch. Expected: 'updatedContent', Actual: '" + result.getContent() + "'"));

        verify(documentRepository).save(any(Document.class));
    }
}