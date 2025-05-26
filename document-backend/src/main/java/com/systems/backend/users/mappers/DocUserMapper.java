package com.systems.backend.users.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.systems.backend.users.models.DocUser;
import com.systems.backend.users.responses.DocUserResponse;

@Mapper(componentModel = "spring")
public interface DocUserMapper {

    @Mapping(source="account.id", target="accountId")
    DocUserResponse toDTO(DocUser docUser);

    default Page<DocUserResponse> toDTOPage(Page<DocUser> docUsers) {
        return docUsers.map(this::toDTO);
    }

}
