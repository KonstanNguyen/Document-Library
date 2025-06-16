package com.systems.backend.users.domains.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systems.backend.common.requests.PaginationRequest;
import com.systems.backend.documents.mappers.DocumentMapper;
import com.systems.backend.documents.models.Document;
import com.systems.backend.documents.responses.DocumentResponse;
import com.systems.backend.documents.services.DocumentService;
import com.systems.backend.users.mappers.UserMapper;
import com.systems.backend.users.models.User;
import com.systems.backend.users.responses.UserResponse;
import com.systems.backend.users.resquests.CreateUserRequest;
import com.systems.backend.users.services.UserService;
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

@WebMvcTest(UserController.class)
@ExtendWith(SpringExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private DocumentMapper documentMapper;

    private User user;
    private UserResponse userResponse;
    private Document document;
    private DocumentResponse documentResponse;
    private final Long userId = 1L;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .name("Test User")
                .email("test@test.com")
                .build();
        userResponse = new UserResponse();
        document = Document.builder()
                .id(1L)
                .author(user)
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
    void getAllDocUsers_ShouldReturnPagedUsers() throws Exception {
        Pageable pageable = PageRequest.of(0, 6, Sort.by("id").ascending());
        List<User> userList = List.of(user);
        Page<User> userPage = new PageImpl<>(userList, pageable, 1);
        Page<UserResponse> responsePage = new PageImpl<>(List.of(userResponse));

        when(userService.getAllDocUsers(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toDTOPage(userPage)).thenReturn(responsePage);

        mockMvc.perform(get("/api/doc-users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(userService).getAllDocUsers(any(Pageable.class));
        verify(userMapper).toDTOPage(userPage);
    }

    @Test
    @WithMockUser
    void getAllDocUsers_WithPaginationRequest_ShouldReturnPagedUsers() throws Exception {
        PaginationRequest pageRequest = new PaginationRequest(1, 10, "name", "desc");
        Pageable pageable = PageRequest.of(1, 10, Sort.by("name").descending());
        List<User> userList = List.of(user);
        Page<User> userPage = new PageImpl<>(userList, pageable, 1);
        Page<UserResponse> responsePage = new PageImpl<>(List.of(userResponse));

        when(userService.getAllDocUsers(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toDTOPage(userPage)).thenReturn(responsePage);

        mockMvc.perform(get("/api/doc-users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(userService).getAllDocUsers(any(Pageable.class));
        verify(userMapper).toDTOPage(userPage);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void createDocUser_ShouldReturnCreatedUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Test User", "test@example.com", "1234567890", "1990-01-01", true);
        when(userService.createDocUser(any(CreateUserRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/doc-users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(userService).createDocUser(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser(authorities = {"user"})
    void createDocUser_WhenNotAdmin_ShouldReturn403() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Test User", "test@example.com", "1234567890", "1990-01-01", true);

        mockMvc.perform(post("/api/doc-users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(userService, never()).createDocUser(any(CreateUserRequest.class));
    }

    @Test
    @WithMockUser
    void getUser_ShouldReturnUser() throws Exception {
        when(userService.getDocUserById(userId)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userResponse);

        mockMvc.perform(get("/api/doc-users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).getDocUserById(userId);
        verify(userMapper).toDTO(user);
    }

    @Test
    @WithMockUser
    void updateDocUser_ShouldReturnUpdatedUser() throws Exception {
        when(userService.updateDocUser(eq(userId), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/doc-users/{userId}/update", userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Test User"));

        verify(userService).updateDocUser(eq(userId), any(User.class));
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void deleteDocUser_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/doc-users/{userId}/delete", userId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).deleteDocUser(userId);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void deleteDocUser_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("User not found"))
                .when(userService).deleteDocUser(userId);

        mockMvc.perform(delete("/api/doc-users/{userId}/delete", userId)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(userService).deleteDocUser(userId);
    }

    @Test
    @WithMockUser(authorities = {"user"})
    void deleteDocUser_WhenNotAdmin_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/doc-users/{userId}/delete", userId)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(userService, never()).deleteDocUser(userId);
    }

    @Test
    @WithMockUser
    void getDocumentsByAuthor_ShouldReturnPagedDocuments() throws Exception {
        Pageable pageable = PageRequest.of(0, 6, Sort.by("createAt").descending());
        List<Document> documentList = List.of(document);
        Page<Document> documentPage = new PageImpl<>(documentList, pageable, 1);
        Page<DocumentResponse> responsePage = new PageImpl<>(List.of(documentResponse));

        when(userService.getDocUserById(userId)).thenReturn(user);
        when(documentService.getDocumentsByAuthor(user, pageable)).thenReturn(documentPage);
        when(documentMapper.toDTOPage(documentPage)).thenReturn(responsePage);

        mockMvc.perform(get("/api/doc-users/{userId}/documents", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(userService).getDocUserById(userId);
        verify(documentService).getDocumentsByAuthor(user, pageable);
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

        when(userService.getDocUserById(userId)).thenReturn(user);
        when(documentService.getDocumentsByAuthor(user, pageable)).thenReturn(documentPage);
        when(documentMapper.toDTOPage(documentPage)).thenReturn(responsePage);

        mockMvc.perform(get("/api/doc-users/{userId}/documents", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(userService).getDocUserById(userId);
        verify(documentService).getDocumentsByAuthor(user, pageable);
        verify(documentMapper).toDTOPage(documentPage);
    }

    @Test
    @WithMockUser
    void getDocumentsByAuthor_WhenUserNotFound_ShouldReturn404() throws Exception {
        when(userService.getDocUserById(userId)).thenReturn(null);

        mockMvc.perform(get("/api/doc-users/{userId}/documents", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService).getDocUserById(userId);
        verify(documentService, never()).getDocumentsByAuthor(any(), any());
    }
}