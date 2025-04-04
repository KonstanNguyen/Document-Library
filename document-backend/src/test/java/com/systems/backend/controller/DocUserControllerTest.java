package com.systems.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systems.backend.mapper.DocUserMapper;
import com.systems.backend.mapper.DocumentMapper;
import com.systems.backend.model.DocUser;
import com.systems.backend.model.Document;
import com.systems.backend.requests.CreateDocUserRequest;
import com.systems.backend.requests.PaginationRequest;
import com.systems.backend.responses.DocUserResponse;
import com.systems.backend.responses.DocumentResponse;
import com.systems.backend.service.DocUserService;
import com.systems.backend.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocUserController.class)
@ExtendWith(SpringExtension.class)
class DocUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocUserService docUserService;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private DocUserMapper docUserMapper;

    @MockitoBean
    private DocumentMapper documentMapper;

    private DocUser docUser;
    private DocUserResponse docUserResponse;
    private Document document;
    private DocumentResponse documentResponse;
    private final Long docUserId = 1L;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        docUser = DocUser.builder()
                .id(docUserId)
                .name("Test User")
                .email("test@test.com")
                .build();
        docUserResponse = new DocUserResponse();
        document = Document.builder()
                .id(1L)
                .author(docUser)
                .title("Test Title")
                .thumbnail("test.pdf")
                .description("Test Description")
                .content("Test Content")
                .build();
        documentResponse = new DocumentResponse();
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser
    void getAllDocUsers_ShouldReturnPagedDocUsers() throws Exception {
        Pageable pageable = PageRequest.of(0, 6, Sort.by("id").ascending());
        List<DocUser> docUserList = List.of(docUser);
        Page<DocUser> docUserPage = new PageImpl<>(docUserList, pageable, 1);
        Page<DocUserResponse> responsePage = new PageImpl<>(List.of(docUserResponse));

        when(docUserService.getAllDocUsers(any(Pageable.class))).thenReturn(docUserPage);
        when(docUserMapper.toDTOPage(docUserPage)).thenReturn(responsePage);

        mockMvc.perform(get("/api/doc-users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(docUserService).getAllDocUsers(any(Pageable.class));
        verify(docUserMapper).toDTOPage(docUserPage);
    }

    @Test
    @WithMockUser
    void getAllDocUsers_WithPaginationRequest_ShouldReturnPagedDocUsers() throws Exception {
        PaginationRequest pageRequest = new PaginationRequest(1, 10, "name", "desc");
        Pageable pageable = PageRequest.of(1, 10, Sort.by("name").descending());
        List<DocUser> docUserList = List.of(docUser);
        Page<DocUser> docUserPage = new PageImpl<>(docUserList, pageable, 1);
        Page<DocUserResponse> responsePage = new PageImpl<>(List.of(docUserResponse));

        when(docUserService.getAllDocUsers(any(Pageable.class))).thenReturn(docUserPage);
        when(docUserMapper.toDTOPage(docUserPage)).thenReturn(responsePage);

        mockMvc.perform(get("/api/doc-users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(docUserService).getAllDocUsers(any(Pageable.class));
        verify(docUserMapper).toDTOPage(docUserPage);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void createDocUser_ShouldReturnCreatedDocUser() throws Exception {
        CreateDocUserRequest request = new CreateDocUserRequest("Test User", "test@example.com", "1234567890", "1990-01-01", true);
        when(docUserService.createDocUser(any(CreateDocUserRequest.class))).thenReturn(docUser);

        mockMvc.perform(post("/api/doc-users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(docUserId))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(docUserService).createDocUser(any(CreateDocUserRequest.class));
    }

    @Test
    @WithMockUser(authorities = {"user"})
    void createDocUser_WhenNotAdmin_ShouldReturn403() throws Exception {
        CreateDocUserRequest request = new CreateDocUserRequest("Test User", "test@example.com", "1234567890", "1990-01-01", true);

        mockMvc.perform(post("/api/doc-users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(docUserService, never()).createDocUser(any(CreateDocUserRequest.class));
    }

    @Test
    @WithMockUser
    void getDocUser_ShouldReturnDocUser() throws Exception {
        when(docUserService.getDocUserById(docUserId)).thenReturn(docUser);
        when(docUserMapper.toDTO(docUser)).thenReturn(docUserResponse);

        mockMvc.perform(get("/api/doc-users/{docUserId}", docUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(docUserService).getDocUserById(docUserId);
        verify(docUserMapper).toDTO(docUser);
    }

    @Test
    @WithMockUser
    void updateDocUser_ShouldReturnUpdatedDocUser() throws Exception {
        when(docUserService.updateDocUser(eq(docUserId), any(DocUser.class))).thenReturn(docUser);

        mockMvc.perform(put("/api/doc-users/{docUserId}/update", docUserId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(docUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(docUserId))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(docUserService).updateDocUser(eq(docUserId), any(DocUser.class));
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void deleteDocUser_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/doc-users/{docUserId}/delete", docUserId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(docUserService).deleteDocUser(docUserId);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void deleteDocUser_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("DocUser not found"))
                .when(docUserService).deleteDocUser(docUserId);

        mockMvc.perform(delete("/api/doc-users/{docUserId}/delete", docUserId)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(docUserService).deleteDocUser(docUserId);
    }

    @Test
    @WithMockUser(authorities = {"user"})
    void deleteDocUser_WhenNotAdmin_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/doc-users/{docUserId}/delete", docUserId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(docUserService, never()).deleteDocUser(docUserId);
    }

    @Test
    @WithMockUser
    void getDocumentsByAuthor_ShouldReturnPagedDocuments() throws Exception {
        Pageable pageable = PageRequest.of(0, 6, Sort.by("createAt").descending());
        List<Document> documentList = List.of(document);
        Page<Document> documentPage = new PageImpl<>(documentList, pageable, 1);
        Page<DocumentResponse> responsePage = new PageImpl<>(List.of(documentResponse));

        when(docUserService.getDocUserById(docUserId)).thenReturn(docUser);
        when(documentService.getDocumentsByAuthor(docUser, pageable)).thenReturn(documentPage);
        when(documentMapper.toDTOPage(documentPage)).thenReturn(responsePage);

        mockMvc.perform(get("/api/doc-users/{docUserId}/documents", docUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(docUserService).getDocUserById(docUserId);
        verify(documentService).getDocumentsByAuthor(docUser, pageable);
        verify(documentMapper).toDTOPage(documentPage);
    }

    @Test
    @WithMockUser
    void getDocumentsByAuthor_WithPaginationRequest_ShouldReturnPagedDocuments() throws Exception {
        PaginationRequest pageRequest = new PaginationRequest(1, 10, "title", "asc");
        Pageable pageable = PageRequest.of(1, 10, Sort.by("title").ascending());
        List<Document> documentList = List.of(document);
        Page<Document> documentPage = new PageImpl<>(documentList, pageable, 1);
        Page<DocumentResponse> responsePage = new PageImpl<>(List.of(documentResponse));

        when(docUserService.getDocUserById(docUserId)).thenReturn(docUser);
        when(documentService.getDocumentsByAuthor(docUser, pageable)).thenReturn(documentPage);
        when(documentMapper.toDTOPage(documentPage)).thenReturn(responsePage);

        mockMvc.perform(get("/api/doc-users/{docUserId}/documents", docUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(docUserService).getDocUserById(docUserId);
        verify(documentService).getDocumentsByAuthor(docUser, pageable);
        verify(documentMapper).toDTOPage(documentPage);
    }

    @Test
    @WithMockUser
    void getDocumentsByAuthor_WhenDocUserNotFound_ShouldReturn404() throws Exception {
        when(docUserService.getDocUserById(docUserId)).thenReturn(null);

        mockMvc.perform(get("/api/doc-users/{docUserId}/documents", docUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(docUserService).getDocUserById(docUserId);
        verify(documentService, never()).getDocumentsByAuthor(any(), any());
    }
}