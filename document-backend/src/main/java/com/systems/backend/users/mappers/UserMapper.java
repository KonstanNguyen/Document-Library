package com.systems.backend.users.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.systems.backend.users.models.User;
import com.systems.backend.users.responses.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source="account.id", target="accountId")
    UserResponse toDTO(User user);

    default Page<UserResponse> toDTOPage(Page<User> users) {
        return users.map(this::toDTO);
    }

}
