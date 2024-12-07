package com.systems.backend.service.impl;

import com.systems.backend.model.Category;
import com.systems.backend.repository.CategoryRepository;
import com.systems.backend.requests.CreateCategoryRequest;
import com.systems.backend.service.CategoryService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category getCategoryById(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryOptional.orElse(null);
    }

    @Override
    public List<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(CreateCategoryRequest createCategoryRequest) {
        if (categoryRepository.existsByName(createCategoryRequest.getName())) {
            throw new RuntimeException("This category has already existed");
        }
        Category role = Category.builder()
                .id(createCategoryRequest.getCategoryId())
                .name(createCategoryRequest.getName())
                .description(createCategoryRequest.getDescription())
                .build();
        return categoryRepository.save(role);
    }

    @Override
    public void deleteCategory(Long id) {
        Category checkCategory = getCategoryById(id);
        if (checkCategory == null) {
            throw new RuntimeException("This category is not found");
        }
        categoryRepository.delete(checkCategory);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        Category updatedCategory = getCategoryById(id);
        if (updatedCategory == null) {
            throw new RuntimeException("This category is not found");
        }
        updatedCategory.setName(category.getName());
        updatedCategory.setDescription(category.getDescription());
        return categoryRepository.save(updatedCategory);
    }
}
