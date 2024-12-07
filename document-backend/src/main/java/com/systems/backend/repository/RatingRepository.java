package com.systems.backend.repository;

import com.systems.backend.model.Account;
import com.systems.backend.model.Document;
import com.systems.backend.model.Rating;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Rating.RatingId> {
    List<Rating> findByRatingId_Account(Account account);
    List<Rating> findByRatingId_Document(Document document);
}
