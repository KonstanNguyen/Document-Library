package com.systems.backend.documents.mappers;

import com.systems.backend.documents.models.Document;
import com.systems.backend.ratings.models.Rating;
import com.systems.backend.documents.responses.DocumentResponse;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.*;
import org.springframework.data.domain.Page;


@Mapper(componentModel= "spring")
public interface DocumentMapper {

    // Map từ Entity sang DTO
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName") 
    @Mapping(source = "author.name", target = "authorName")
    @Mapping(target="ratingAvg", expression = "java(calculateRatingAvg(document.getRatings()))")
    DocumentResponse toDTO(Document document);

    default Float calculateRatingAvg(Collection<Rating> documentRatings) {
        Set<Rating> ratings = documentRatings.stream().collect(Collectors.toSet());
        if (ratings == null || ratings.isEmpty()) {
            return 0.0f;
        }

        float totalRating = 0.0f;
        for (Rating rating : ratings) {
            totalRating += rating.getRate();
        }

        return totalRating / ratings.size();
    }

    default Page<DocumentResponse> toDTOPage(Page<Document> documents) {
        return documents.map(this::toDTO);
    }
}

