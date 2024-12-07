package com.systems.backend.service;

import com.systems.backend.model.Account;
import com.systems.backend.requests.LoginRequest;
import com.systems.backend.requests.RegisterRequest;
import com.systems.backend.responses.LoginResponse;
import com.systems.backend.responses.RegisterResponse;

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
}
