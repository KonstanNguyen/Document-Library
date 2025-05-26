package com.systems.backend.users.services;

import com.systems.backend.users.models.Account;
import com.systems.backend.users.resquests.LoginRequest;
import com.systems.backend.users.resquests.RegisterRequest;
import com.systems.backend.users.responses.LoginResponse;
import com.systems.backend.users.responses.RegisterResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    Boolean existsByUsername(String username);
    Account getAccountById(Long id);
    Account getAccountByUsername(String username);
    Page<Account> getAllAccounts(Pageable pageable);
    Account createAccount(Account account);
    Account updateAccount(Long accountId, Account account);
    void deleteAccount(Long id);
    RegisterResponse registerAccount(RegisterRequest registerRequest);
    LoginResponse loginAccount(LoginRequest loginRequest);
//    void rateDocument(CreateRatingRequest createRatingRequest);
}
