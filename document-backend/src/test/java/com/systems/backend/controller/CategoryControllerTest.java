package com.systems.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systems.backend.documents.domains.user.CategoryController;
import com.systems.backend.documents.mappers.DocumentMapper;
import com.systems.backend.documents.models.Category;
import com.systems.backend.documents.models.Document;
import com.systems.backend.documents.resquests.CreateCategoryRequest;
import com.systems.backend.common.requests.PaginationRequest;
import com.systems.backend.documents.responses.DocumentResponse;
import com.systems.backend.documents.services.CategoryService;
import com.systems.backend.documents.services.DocumentService;
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

@WebMvcTest(CategoryController.class)
@ExtendWith(SpringExtension.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private DocumentMapper documentMapper;

    private Category category;
    private Document document;
    private DocumentResponse documentResponse;
    private final Long categoryId = 1L;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        category = new Category(categoryId, "Test Category", "Test Description", null);
        document = Document.builder()
                .id(1L)
                .category(category)
                .title("Test Title")
                .thumbnail("test.pdf")
                .description("Test Description")
                .content("Test Content")
                .build();
        documentResponse = new DocumentResponse(); // Assuming this exists
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void getAllCategory_ShouldReturnCategories() throws Exception {
        List<Category> categories = List.of(category);
        when(categoryService.getAllCategory()).thenReturn(categories);

        mockMvc.perform(get("/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(categoryId))
                .andExpect(jsonPath("$[0].name").value("Test Category"));

        verify(categoryService).getAllCategory();
    }

    @Test
    @WithMockUser(authorities = {"user"})
    void getAllCategory_WhenNotAdmin_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(categoryService, never()).getAllCategory();
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest(categoryId, "Test Category", "Test Description");
        when(categoryService.createCategory(any(CreateCategoryRequest.class))).thenReturn(category);

        mockMvc.perform(post("/category")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Test Category"));

        verify(categoryService).createCategory(any(CreateCategoryRequest.class));
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void getCategory_ShouldReturnCategory() throws Exception {
        when(categoryService.getCategoryById(categoryId)).thenReturn(category);

        mockMvc.perform(get("/category/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Test Category"));

        verify(categoryService).getCategoryById(categoryId);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void getCategory_WhenNotFound_ShouldReturn404() throws Exception {
        when(categoryService.getCategoryById(categoryId)).thenReturn(null);

        mockMvc.perform(get("/category/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService).getCategoryById(categoryId);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        when(categoryService.updateCategory(eq(categoryId), any(Category.class))).thenReturn(category);

        mockMvc.perform(put("/category/{categoryId}/update", categoryId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Test Category"));

        verify(categoryService).updateCategory(eq(categoryId), any(Category.class));
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void deleteCategory_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/category/{categoryId}/delete", categoryId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(categoryId);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void deleteCategory_WhenNotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Category not found"))
                .when(categoryService).deleteCategory(categoryId);

        mockMvc.perform(delete("/category/{categoryId}/delete", categoryId)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(categoryService).deleteCategory(categoryId);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void getDocumentsByCategory_ShouldReturnPagedDocuments() throws Exception {
        Pageable pageable = PageRequest.of(0, 6, Sort.by("createAt").descending());
        List<Document> documentList = List.of(document);
        Page<Document> documentPage = new PageImpl<>(documentList, pageable, 1);
        Page<DocumentResponse> responsePage = new PageImpl<>(List.of(documentResponse));

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(documentService.getDocumentByCategory(category, pageable)).thenReturn(documentPage);
        when(documentMapper.toDTOPage(documentPage)).thenReturn(responsePage);

        mockMvc.perform(get("/category/{categoryId}/documents", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(categoryService).getCategoryById(categoryId);
        verify(documentService).getDocumentByCategory(category, pageable);
        verify(documentMapper).toDTOPage(documentPage);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void getDocumentsByCategory_WithPaginationRequest_ShouldReturnPagedDocuments() throws Exception {
        PaginationRequest pageRequest = new PaginationRequest(1, 10, "title", "asc");
        Pageable pageable = PageRequest.of(1, 10, Sort.by("title").ascending());
        List<Document> documentList = List.of(document);
        Page<Document> documentPage = new PageImpl<>(documentList, pageable, 1);
        Page<DocumentResponse> responsePage = new PageImpl<>(List.of(documentResponse));

        when(categoryService.getCategoryById(categoryId)).thenReturn(category);
        when(documentService.getDocumentByCategory(category, pageable)).thenReturn(documentPage);
        when(documentMapper.toDTOPage(documentPage)).thenReturn(responsePage);

        mockMvc.perform(get("/category/{categoryId}/documents", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(categoryService).getCategoryById(categoryId);
        verify(documentService).getDocumentByCategory(category, pageable);
        verify(documentMapper).toDTOPage(documentPage);
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void getDocumentsByCategory_WhenCategoryNotFound_ShouldReturn404() throws Exception {
        when(categoryService.getCategoryById(categoryId)).thenReturn(null);

        mockMvc.perform(get("/category/{categoryId}/documents", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoryService).getCategoryById(categoryId);
        verify(documentService, never()).getDocumentByCategory(any(), any());
    }
}