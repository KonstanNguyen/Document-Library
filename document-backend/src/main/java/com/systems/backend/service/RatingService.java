package com.systems.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.systems.backend.requests.CreateRatingRequest;
import com.systems.backend.responses.RatingResponse;

@Service
public interface RatingService {
        
    List<RatingResponse> getAllRatings();

    List<RatingResponse> getRatingByAccountId(Long accountId);

    List<RatingResponse> getRatingByDocumentId(Long documentId);

    RatingResponse createRating(CreateRatingRequest createRatingRequest);
}
