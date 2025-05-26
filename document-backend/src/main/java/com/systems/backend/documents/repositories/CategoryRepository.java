package com.systems.backend.documents.repositories;

import com.systems.backend.documents.models.Category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByName(String name);
    Boolean existsByName(String name);
}
