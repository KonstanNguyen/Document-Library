package com.systems.backend.users.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.systems.backend.users.models.Account;
import com.systems.backend.users.responses.AccountResponse;

@Mapper(componentModel="spring")
public interface AccountMapper {
    
    @Mapping(source="user.id", target="userId")
    AccountResponse toDTO(Account account);

    default Page<AccountResponse> toDTOPage(Page<Account> accounts) {
        return accounts.map(this::toDTO);
    }
}
