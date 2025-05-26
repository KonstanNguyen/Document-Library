package com.systems.backend.ratings.repositories;

import com.systems.backend.users.models.Account;
import com.systems.backend.documents.models.Document;
import com.systems.backend.ratings.models.Rating;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Rating.RatingId> {
    List<Rating> findByRatingId_Account(Account account);
    List<Rating> findByRatingId_Document(Document document);
}
