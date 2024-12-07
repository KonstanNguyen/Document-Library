package com.systems.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import com.systems.backend.requests.CreateRatingRequest;
import com.systems.backend.responses.RatingResponse;
import com.systems.backend.service.RatingService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("api/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RatingResponse> getAllRatings() {
        return ratingService.getAllRatings();
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public RatingResponse createRating(@Valid @RequestBody CreateRatingRequest ratingRequest) {
        return ratingService.createRating(ratingRequest);
    } 
}
