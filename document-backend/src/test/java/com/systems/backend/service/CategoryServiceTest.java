package com.systems.backend.service;

import com.systems.backend.documents.models.Category;
import com.systems.backend.documents.repositories.CategoryRepository;
import com.systems.backend.documents.resquests.CreateCategoryRequest;
import com.systems.backend.documents.services.impl.CategoryServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private final long CATEGORY_ID = 1L;
    private final String CATEGORY_NAME = "category1";
    private List<Category> categories = List.of(
            new Category(1L, null, "category1", null),
            new Category(2L, null, "category2", null),
            new Category(3L, null, "category3", null));

    private Category category;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        category = new Category(1L, null, "category1", null);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCategoryById_whenCategoryExists_shouldReturnCategory() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));

        Category existingCategory = categoryService.getCategoryById(CATEGORY_ID);

        assertAll(
                () -> assertNotNull(existingCategory, "The category should not be null"),
                () -> assertEquals("category1", existingCategory.getName(), "The category name should match"));

        verify(categoryRepository).findById(CATEGORY_ID);
    }

    @Test
    void getCategoryById_whenCategoryDoesNotExist_shouldThrowResourceNotFoundException() {
        long nonExistingCategoryId = 10L;
        when(categoryRepository.findById(nonExistingCategoryId)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById(nonExistingCategoryId);
        });

        assertEquals("Category is not found!", thrown.getMessage(),
                "Exception message should match");

        verify(categoryRepository).findById(nonExistingCategoryId);
    }

    @Test
    void getCategoryById_whenIdIsNegative_shouldThrowIllegalArgumentException() {
        long negativeId = -1L;

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.getCategoryById(negativeId);
        });

        assertEquals("Id cannot be negative", thrown.getMessage(),
                "Exception message should match");
    }

    @Test
    void getCategoryByName_whenCategoriesExist_shouldReturnCategoryList() {
        when(categoryRepository.findByName(CATEGORY_NAME)).thenReturn(List.of(category));

        List<Category> result = categoryService.getCategoryByName(CATEGORY_NAME);

        assertAll(
                () -> assertNotNull(result, "The result should not be null"),
                () -> assertEquals(1, result.size(), "List should contain one category"),
                () -> assertEquals(CATEGORY_NAME, result.get(0).getName(), "The category name should match"));

        verify(categoryRepository).findByName(CATEGORY_NAME);
    }

    @Test
    void getAllCategory_whenCategoriesExist_shouldReturnAllCategories() {
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategory();

        System.out.println("Total categories retrieved: " + result.size());
        result.forEach(category -> System.out.println(" - Category name: " + category.getName()));

        assertAll(
                () -> assertNotNull(result, "Result list should not be null"),
                () -> assertEquals(3, result.size(), "Should return exactly 3 categories"),
                () -> assertEquals("category1", result.get(0).getName(),
                        "First category name should be 'category1'"));

        verify(categoryRepository).findAll();
    }

    @Test
    void createCategory_whenCategoryDoesNotExist_shouldCreateSuccessfully() {
        CreateCategoryRequest request = new CreateCategoryRequest(CATEGORY_ID, "category1", "description");
        when(categoryRepository.existsByName(request.getName())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category createdCategory = categoryService.createCategory(request);

        assertAll(
                () -> assertNotNull(createdCategory, "Created category should not be null"),
                () -> assertEquals("category1", createdCategory.getName(), "Category name should match"));

        verify(categoryRepository).existsByName(request.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_whenCategoryAlreadyExists_shouldThrowIllegalStateException() {
        CreateCategoryRequest request = new CreateCategoryRequest(CATEGORY_ID, "category1", "description");
        when(categoryRepository.existsByName(request.getName())).thenReturn(true);

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            categoryService.createCategory(request);
        });

        assertEquals("This category has already existed", thrown.getMessage(),
                "Exception message should match");

        verify(categoryRepository).existsByName(request.getName());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_whenCategoryExists_shouldDeleteSuccessfully() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        categoryService.deleteCategory(CATEGORY_ID);

        verify(categoryRepository).findById(CATEGORY_ID);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_whenCategoryDoesNotExist_shouldThrowResourceNotFoundException() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.deleteCategory(CATEGORY_ID);
        });

        assertEquals("Category is not found!", thrown.getMessage(),
                "Exception message should match");

        verify(categoryRepository).findById(CATEGORY_ID);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void updateCategory_whenCategoryExists_shouldUpdateSuccessfully() {
        Category updatedDetails = new Category(CATEGORY_ID, "updatedDesc", "updatedName", null);
        Category updatedCategory = new Category(CATEGORY_ID, "updatedDesc", "updatedName", null);
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryService.updateCategory(CATEGORY_ID, updatedDetails);

        assertAll(
                () -> assertNotNull(result, "Updated category should not be null"),
                () -> assertEquals("updatedName", result.getName(), "Category name should be updated"),
                () -> assertEquals("updatedDesc", result.getDescription(), "Category description should be updated"));

        verify(categoryRepository).findById(CATEGORY_ID);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_whenCategoryDoesNotExist_shouldThrowResourceNotFoundException() {
        Category updatedDetails = new Category(CATEGORY_ID, "updatedDesc", "updatedName", null);
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.updateCategory(CATEGORY_ID, updatedDetails);
        });

        assertEquals("Category is not found!", thrown.getMessage(),
                "Exception message should match");

        verify(categoryRepository).findById(CATEGORY_ID);
        verify(categoryRepository, never()).save(any(Category.class));
    }
}