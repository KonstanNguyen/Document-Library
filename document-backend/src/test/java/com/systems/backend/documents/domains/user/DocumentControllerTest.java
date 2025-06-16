package com.systems.backend.documents.domains.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systems.backend.common.requests.PaginationRequest;
import com.systems.backend.common.utils.UploadResult;
import com.systems.backend.documents.mappers.DocumentMapper;
import com.systems.backend.documents.models.Document;
import com.systems.backend.documents.responses.DocumentResponse;
import com.systems.backend.documents.resquests.CreateDocumentRequest;
import com.systems.backend.documents.services.DocumentService;
import com.systems.backend.download.responses.HistoryDownloadResponse;
import com.systems.backend.download.services.HistoryDownloadService;
import com.systems.backend.ratings.responses.RatingResponse;
import com.systems.backend.ratings.services.RatingService;
import com.systems.backend.upload.services.UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@ExtendWith(SpringExtension.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private UploadService uploadService;

    @MockitoBean
    private RatingService ratingService;

    @MockitoBean
    private HistoryDownloadService historyDownloadService;

    @MockitoBean
    private DocumentMapper documentMapper;

    private Document document;
    private DocumentResponse documentResponse;
    private final Long documentId = 1L;
    private UploadResult uploadResult;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        document = Document.builder()
                .id(documentId)
                .title("Test Title")
                .thumbnail("test.pdf")
                .description("Test Description")
                .content("Test Content")
                .build();
        documentResponse = new DocumentResponse();
        uploadResult = new UploadResult("content/path.pdf", "thumbnail/path.jpg");
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser
    void getAllDocuments_ShouldReturnPagedDocuments() throws Exception {
        Pageable pageable = PageRequest.of(0, 6, Sort.by("createAt").descending());
        List<Document> documentList = List.of(document);
        Page<Document> documentPage = new PageImpl<>(documentList, pageable, 1);
        Page<DocumentResponse> responsePage = new PageImpl<>(List.of(documentResponse));

        when(documentService.getAllDocuments(any(Pageable.class))).thenReturn(documentPage);
        when(documentMapper.toDTOPage(documentPage)).thenReturn(responsePage);

        mockMvc.perform(get("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(documentService).getAllDocuments(any(Pageable.class));
        verify(documentMapper).toDTOPage(documentPage);
    }

    @Test
    @WithMockUser
    void getAllDocuments_WithPaginationRequest_ShouldReturnPagedDocuments() throws Exception {
        PaginationRequest pageRequest = new PaginationRequest(1, 10, "title", "asc");
        Pageable pageable = PageRequest.of(1, 10, Sort.by("title").ascending());
        List<Document> documentList = List.of(document);
        Page<Document> documentPage = new PageImpl<>(documentList, pageable, 1);
        Page<DocumentResponse> responsePage = new PageImpl<>(List.of(documentResponse));

        when(documentService.getAllDocuments(any(Pageable.class))).thenReturn(documentPage);
        when(documentMapper.toDTOPage(documentPage)).thenReturn(responsePage);

        mockMvc.perform(get("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(documentService).getAllDocuments(any(Pageable.class));
        verify(documentMapper).toDTOPage(documentPage);
    }

    @Test
    @WithMockUser
    void createDocument_ShouldReturnCreatedDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());
        CreateDocumentRequest request = CreateDocumentRequest.builder()
                .title("Test Doc")
                .description("Description")
                .categoryId(1L)
                .content("Test Content")
                .thumbnail("test.pdf")
                .build();
        MockMultipartFile dataPart = new MockMultipartFile("data", "", "application/json",
                objectMapper.writeValueAsString(request).getBytes());

        when(uploadService.processFile(any(MultipartFile.class))).thenReturn(uploadResult);
        when(documentService.createDocument(any(CreateDocumentRequest.class))).thenReturn(document);

        mockMvc.perform(multipart("/api/documents")
                        .file(file)
                        .file(dataPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(documentId))
                .andExpect(jsonPath("$.title").value("Test Doc"));

        verify(uploadService).processFile(any(MultipartFile.class));
        verify(documentService).createDocument(any(CreateDocumentRequest.class));
    }

    @Test
    @WithMockUser
    void getDocument_ShouldReturnDocument() throws Exception {
        when(documentService.getDocumentById(documentId)).thenReturn(document);
        when(documentMapper.toDTO(document)).thenReturn(documentResponse);

        mockMvc.perform(get("/api/documents/{documentId}", documentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(documentService).getDocumentById(documentId);
        verify(documentMapper).toDTO(document);
    }

    @Test
    @WithMockUser
    void updateDocument_ShouldReturnUpdatedDocument() throws Exception {
        when(documentService.updateDocument(eq(documentId), any(Document.class))).thenReturn(document);

        mockMvc.perform(put("/api/documents/{documentId}/update", documentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(document)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(documentId))
                .andExpect(jsonPath("$.title").value("Test Doc"));

        verify(documentService).updateDocument(eq(documentId), any(Document.class));
    }

    @Test
    @WithMockUser
    void deleteDocument_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/documents/{documentId}/delete", documentId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(documentService).deleteDocument(documentId);
    }

    @Test
    @WithMockUser
    void deleteDocument_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Document not found"))
                .when(documentService).deleteDocument(documentId);

        mockMvc.perform(delete("/api/documents/{documentId}/delete", documentId)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(documentService).deleteDocument(documentId);
    }

    @Test
    @WithMockUser
    void getRatingsByDocument_ShouldReturnRatings() throws Exception {
        List<RatingResponse> ratings = List.of(new RatingResponse());
        when(ratingService.getRatingByDocumentId(documentId)).thenReturn(ratings);

        mockMvc.perform(get("/api/documents/{documentId}/ratings", documentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(ratingService).getRatingByDocumentId(documentId);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void getHistoryByDocumentId_ShouldReturnHistory() throws Exception {
        List<HistoryDownloadResponse> history = List.of(new HistoryDownloadResponse());
        when(historyDownloadService.getHistoryByDocumentId(documentId)).thenReturn(history);

        mockMvc.perform(get("/api/documents/{documentId}/list-account", documentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(historyDownloadService).getHistoryByDocumentId(documentId);
    }

    @Test
    @WithMockUser(authorities = {"user"})
    void getHistoryByDocumentId_WhenNotAdmin_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/documents/{documentId}/list-account", documentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(historyDownloadService, never()).getHistoryByDocumentId(documentId);
    }
}