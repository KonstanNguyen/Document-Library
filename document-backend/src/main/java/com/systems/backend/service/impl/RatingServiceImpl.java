package com.systems.backend.service.impl;

import com.systems.backend.mapper.RatingMapper;
import com.systems.backend.model.Account;
import com.systems.backend.model.Document;
import com.systems.backend.model.Rating;
import com.systems.backend.repository.AccountRepository;
import com.systems.backend.repository.DocumentRepository;
import com.systems.backend.repository.RatingRepository;
import com.systems.backend.requests.CreateRatingRequest;
import com.systems.backend.responses.RatingResponse;
import com.systems.backend.service.RatingService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceImpl implements RatingService {
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private RatingMapper ratingMapper;

            
    public List<RatingResponse> getAllRatings() {
        return ratingRepository.findAll().stream().map(
            rating -> ratingMapper.toDTO(rating)
        ).toList();
    }

    public List<RatingResponse> getRatingByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new ResourceNotFoundException("Account " + accountId + " not found")
        );
        List<Rating> ratings = ratingRepository.findByRatingId_Account(account);

        return ratings.stream().map(ratingMapper::toDTO).toList();
    }

    public List<RatingResponse> getRatingByDocumentId(Long documentId) {
        Document document = documentRepository.findById(documentId).orElseThrow(() ->
                new ResourceNotFoundException("Document " + documentId + " not found")
        );

        List<Rating> ratings = ratingRepository.findByRatingId_Document(document);

        return ratings.stream().map(ratingMapper::toDTO).toList();
    }

    public RatingResponse createRating(CreateRatingRequest createRatingRequest) {
        Document document = documentRepository.findById(createRatingRequest.getDocumentId()).orElseThrow(() ->
                new ResourceNotFoundException("Document "  + " not found")
        );
        Account account = accountRepository.findById(createRatingRequest.getAccountId()).orElseThrow(() ->
                new ResourceNotFoundException("Account " +  " not found")
        );

        Rating.RatingId id = Rating.RatingId.builder()
            .account(account)
            .document(document)
            .build();

        Rating rating = Rating.builder()
            .ratingId(id)
            .rate(createRatingRequest.getRate())
            .build();

        return ratingMapper.toDTO(ratingRepository.save(rating));
    }
}
