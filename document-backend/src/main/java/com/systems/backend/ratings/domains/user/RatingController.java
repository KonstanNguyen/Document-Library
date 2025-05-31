package com.systems.backend.ratings.domains.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.systems.backend.ratings.resquests.CreateRatingRequest;
import com.systems.backend.ratings.responses.RatingResponse;
import com.systems.backend.ratings.services.RatingService;
import com.systems.backend.common.services.ExportStrategy;
import com.systems.backend.common.services.ExportStrategyFactory;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("api/ratings")
public class RatingController {
    @Autowired
    private RatingService ratingService;

    @PreAuthorize("hasAnyAuthority('admin') or hasAnyAuthority('ADMIN')")
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

    @PreAuthorize("hasAnyAuthority('admin') or hasAnyAuthority('ADMIN')")
    @GetMapping("/export/{format}")
    @ResponseStatus(HttpStatus.OK)
    public void exportRatings(
            @PathVariable String format,
            @RequestParam(required = false) String fileName,
            HttpServletResponse response) {
        String exportFileName = fileName != null ? fileName : "ratings";
        List<RatingResponse> ratings = ratingService.getAllRatings();

        ExportStrategyFactory<RatingResponse> factory = new ExportStrategyFactory<>();
        ExportStrategy<RatingResponse> strategy = factory.getExportStrategy(format, response, exportFileName);
        strategy.export(ratings);
    }
}
