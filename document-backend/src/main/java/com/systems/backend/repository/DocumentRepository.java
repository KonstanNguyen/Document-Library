package com.systems.backend.repository;

import com.systems.backend.model.Category;
import com.systems.backend.model.DocUser;
import com.systems.backend.model.Document;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;


@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Page<Document> findByCategory(Category category, Pageable page);
    Page<Document> findByAuthor(DocUser author, Pageable pageable);
    List<Document> findByTitleContaining(String keywords);
    List<Document> findByStatus(Short status);
    List<Document> findByCreateAt(LocalDateTime createAt);
    Boolean existsByTitle(String title);
}
