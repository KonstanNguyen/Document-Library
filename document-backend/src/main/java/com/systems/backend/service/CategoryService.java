package com.systems.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.systems.backend.model.Category;
import com.systems.backend.requests.CreateCategoryRequest;

@Service
public interface CategoryService {
    Category getCategoryById(long id);
    List<Category> getCategoryByName(String name);
    List<Category> getAllCategory();
    Category createCategory(CreateCategoryRequest createCategoryRequest);
    void deleteCategory(long id);
    Category updateCategory(long id, Category category);
}
