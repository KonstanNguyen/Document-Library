package com.systems.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.systems.backend.model.Category;
import com.systems.backend.requests.CreateCategoryRequest;

@Service
public interface CategoryService {
    Category getCategoryById(Long id);
    List<Category> getCategoryByName(String name);
    List<Category> getAllCategory();
    Category createCategory(CreateCategoryRequest createCategoryRequest);
    void deleteCategory(Long id);
    Category updateCategory(Long id, Category category);
}
