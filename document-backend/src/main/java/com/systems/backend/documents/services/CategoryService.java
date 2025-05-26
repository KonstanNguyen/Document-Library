package com.systems.backend.documents.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.systems.backend.documents.models.Category;
import com.systems.backend.documents.resquests.CreateCategoryRequest;

@Service
public interface CategoryService {
    Category getCategoryById(long id);
    List<Category> getCategoryByName(String name);
    List<Category> getAllCategory();
    Category createCategory(CreateCategoryRequest createCategoryRequest);
    void deleteCategory(long id);
    Category updateCategory(long id, Category category);
}
