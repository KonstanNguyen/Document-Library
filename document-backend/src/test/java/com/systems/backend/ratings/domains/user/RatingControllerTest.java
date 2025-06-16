package com.systems.backend.ratings.domains.user;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.systems.backend.ratings.responses.RatingResponse;
import com.systems.backend.ratings.resquests.CreateRatingRequest;
import com.systems.backend.ratings.services.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
@ExtendWith(SpringExtension.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RatingService ratingService;

    private RatingResponse ratingResponse;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ratingResponse = new RatingResponse(); // Assuming this has appropriate fields
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(authorities = {"admin"})
    void getAllRatings_ShouldReturnRatings() throws Exception {
        List<RatingResponse> ratings = List.of(ratingResponse);
        when(ratingService.getAllRatings()).thenReturn(ratings);

        mockMvc.perform(get("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(ratingService).getAllRatings();
    }

    @Test
    @WithMockUser(authorities = {"user"})
    void getAllRatings_WhenNotAdmin_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(ratingService, never()).getAllRatings();
    }

    @Test
    @WithMockUser
    void createRating_ShouldReturnCreatedRating() throws Exception {
        CreateRatingRequest request = new CreateRatingRequest(1L, 2L, 5);
        when(ratingService.createRating(any(CreateRatingRequest.class))).thenReturn(ratingResponse);

        mockMvc.perform(post("/api/ratings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(ratingService).createRating(any(CreateRatingRequest.class));
    }

    @Test
    @WithMockUser
    void createRating_WithInvalidData_ShouldReturn400() throws Exception {
        CreateRatingRequest invalidRequest = new CreateRatingRequest(null, null, -1); // Assuming validation fails for negative rating

        mockMvc.perform(post("/api/ratings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(ratingService, never()).createRating(any(CreateRatingRequest.class));
    }
}