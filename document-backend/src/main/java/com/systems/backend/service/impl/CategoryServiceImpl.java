package com.systems.backend.service.impl;

import com.systems.backend.model.Category;
import com.systems.backend.repository.CategoryRepository;
import com.systems.backend.requests.CreateCategoryRequest;
import com.systems.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category getCategoryById(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id cannot be negative");
        }

        return categoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category is not found!")
        );
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
            throw new IllegalStateException("This category has already existed");
        }
        Category role = Category.builder()
                .id(createCategoryRequest.getCategoryId())
                .name(createCategoryRequest.getName())
                .description(createCategoryRequest.getDescription())
                .build();
        return categoryRepository.save(role);
    }

    @Override
    public void deleteCategory(long id) {
        Category checkCategory = getCategoryById(id);
        if (checkCategory == null) {
            throw new ResourceNotFoundException("This category is not found");
        }
        categoryRepository.delete(checkCategory);
    }

    @Override
    public Category updateCategory(long id, Category category) {
        Category updatedCategory = getCategoryById(id);
        if (updatedCategory == null) {
            throw new ResourceNotFoundException("This category is not found");
        }
        updatedCategory.setName(category.getName());
        updatedCategory.setDescription(category.getDescription());
        return categoryRepository.save(updatedCategory);
    }
}
