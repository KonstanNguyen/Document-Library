package com.systems.backend.ratings.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.systems.backend.ratings.resquests.CreateRatingRequest;
import com.systems.backend.ratings.responses.RatingResponse;

@Service
public interface RatingService {
        
    List<RatingResponse> getAllRatings();

    List<RatingResponse> getRatingByAccountId(Long accountId);

    List<RatingResponse> getRatingByDocumentId(Long documentId);

    RatingResponse createRating(CreateRatingRequest createRatingRequest);
}
