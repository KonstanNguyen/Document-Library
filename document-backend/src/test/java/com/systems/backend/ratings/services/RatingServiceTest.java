package com.systems.backend.ratings.services;

import com.systems.backend.documents.models.Document;
import com.systems.backend.documents.repositories.DocumentRepository;
import com.systems.backend.ratings.mappers.RatingMapper;
import com.systems.backend.ratings.models.Rating;
import com.systems.backend.ratings.repositories.RatingRepository;
import com.systems.backend.ratings.responses.RatingResponse;
import com.systems.backend.ratings.resquests.CreateRatingRequest;
import com.systems.backend.ratings.services.impl.RatingServiceImpl;
import com.systems.backend.users.models.Account;
import com.systems.backend.users.repositories.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {
    private final Long ACCOUNT_ID = 1L;
    private final Long DOCUMENT_ID = 1L;
    private Account account;
    private Document document;
    private Rating rating;
    private RatingResponse ratingResponse;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private RatingMapper ratingMapper;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @BeforeEach
    void setUp() {
        account = new Account(ACCOUNT_ID, "testUser", "password", null, null, null, null);
        document = Document.builder()
                .id(DOCUMENT_ID)
                .title("testDoc")
                .build();

        Rating.RatingId ratingId = Rating.RatingId.builder()
                .account(account)
                .document(document)
                .build();

        rating = Rating.builder()
                .ratingId(ratingId)
                .rate((short) 5)
                .build();

        ratingResponse = new RatingResponse(); // Assuming this exists
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllRatings() {
        List<Rating> ratings = List.of(rating);
        when(ratingRepository.findAll()).thenReturn(ratings);
        when(ratingMapper.toDTO(rating)).thenReturn(ratingResponse);

        List<RatingResponse> result = ratingService.getAllRatings();

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(), "Should contain one rating"),
                () -> assertEquals(ratingResponse, result.get(0), "Response should match mapped DTO")
        );

        verify(ratingRepository).findAll();
        verify(ratingMapper).toDTO(rating);
    }

    @Test
    void getRatingByAccountId_whenAccountExists_shouldReturnRatings() {
        List<Rating> ratings = List.of(rating);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(ratingRepository.findByRatingId_Account(account)).thenReturn(ratings);
        when(ratingMapper.toDTO(rating)).thenReturn(ratingResponse);

        List<RatingResponse> result = ratingService.getRatingByAccountId(ACCOUNT_ID);

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(), "Should contain one rating"),
                () -> assertEquals(ratingResponse, result.get(0), "Response should match mapped DTO")
        );

        verify(accountRepository).findById(ACCOUNT_ID);
        verify(ratingRepository).findByRatingId_Account(account);
        verify(ratingMapper).toDTO(rating);
    }

    @Test
    void getRatingByAccountId_whenAccountDoesNotExist_shouldThrowResourceNotFoundException() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.getRatingByAccountId(ACCOUNT_ID);
        });

        assertEquals("Account " + ACCOUNT_ID + " not found", thrown.getMessage());

        verify(accountRepository).findById(ACCOUNT_ID);
        verify(ratingRepository, never()).findByRatingId_Account(any());
        verify(ratingMapper, never()).toDTO(any());
    }

    @Test
    void getRatingByDocumentId_whenDocumentExists_shouldReturnRatings() {
        List<Rating> ratings = List.of(rating);
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        when(ratingRepository.findByRatingId_Document(document)).thenReturn(ratings);
        when(ratingMapper.toDTO(rating)).thenReturn(ratingResponse);

        List<RatingResponse> result = ratingService.getRatingByDocumentId(DOCUMENT_ID);

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1, result.size(), "Should contain one rating"),
                () -> assertEquals(ratingResponse, result.get(0), "Response should match mapped DTO")
        );

        verify(documentRepository).findById(DOCUMENT_ID);
        verify(ratingRepository).findByRatingId_Document(document);
        verify(ratingMapper).toDTO(rating);
    }

    @Test
    void getRatingByDocumentId_whenDocumentDoesNotExist_shouldThrowResourceNotFoundException() {
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.getRatingByDocumentId(DOCUMENT_ID);
        });

        assertEquals("Document " + DOCUMENT_ID + " not found", thrown.getMessage());

        verify(documentRepository).findById(DOCUMENT_ID);
        verify(ratingRepository, never()).findByRatingId_Document(any());
        verify(ratingMapper, never()).toDTO(any());
    }

    @Test
    void createRating_whenAccountAndDocumentExist_shouldCreateSuccessfully() {
        CreateRatingRequest request = new CreateRatingRequest(ACCOUNT_ID, DOCUMENT_ID, 5);
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);
        when(ratingMapper.toDTO(rating)).thenReturn(ratingResponse);

        RatingResponse result = ratingService.createRating(request);

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(ratingResponse, result, "Response should match mapped DTO")
        );

        verify(documentRepository).findById(DOCUMENT_ID);
        verify(accountRepository).findById(ACCOUNT_ID);
        verify(ratingRepository).save(any(Rating.class));
        verify(ratingMapper).toDTO(rating);
    }

    @Test
    void createRating_whenDocumentDoesNotExist_shouldThrowResourceNotFoundException() {
        CreateRatingRequest request = new CreateRatingRequest(ACCOUNT_ID, DOCUMENT_ID, 5);
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.createRating(request);
        });

        assertEquals("Document  not found", thrown.getMessage());

        verify(documentRepository).findById(DOCUMENT_ID);
        verify(accountRepository, never()).findById(any());
        verify(ratingRepository, never()).save(any());
        verify(ratingMapper, never()).toDTO(any());
    }

    @Test
    void createRating_whenAccountDoesNotExist_shouldThrowResourceNotFoundException() {
        CreateRatingRequest request = new CreateRatingRequest(ACCOUNT_ID, DOCUMENT_ID, 5);
        when(documentRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            ratingService.createRating(request);
        });

        assertEquals("Account  not found", thrown.getMessage());

        verify(documentRepository).findById(DOCUMENT_ID);
        verify(accountRepository).findById(ACCOUNT_ID);
        verify(ratingRepository, never()).save(any());
        verify(ratingMapper, never()).toDTO(any());
    }
}