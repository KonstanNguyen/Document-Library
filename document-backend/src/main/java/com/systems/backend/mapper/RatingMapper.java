package com.systems.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.systems.backend.model.Rating;
import com.systems.backend.responses.RatingResponse;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(source = "ratingId.account.id", target="accountId")
    @Mapping(source = "ratingId.document.id", target="documentId")
    RatingResponse toDTO(Rating rating);
}
