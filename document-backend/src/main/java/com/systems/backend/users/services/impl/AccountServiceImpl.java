package com.systems.backend.users.services.impl;

import com.systems.backend.common.constants.JwtConstants;
import com.systems.backend.users.models.Account;
import com.systems.backend.users.models.User;
import com.systems.backend.users.models.Role;
import com.systems.backend.users.repositories.AccountRepository;
import com.systems.backend.users.repositories.RoleRepository;
import com.systems.backend.users.resquests.LoginRequest;
import com.systems.backend.users.resquests.RegisterRequest;
import com.systems.backend.users.responses.LoginResponse;
import com.systems.backend.users.responses.RegisterResponse;
import com.systems.backend.common.security.JwtGenerator;
import com.systems.backend.users.services.AccountService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtGenerator jwtGenerator;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Boolean existsByUsername(String username) {
        if (username == null || username.isBlank() || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        return accountRepository.existsByUsername(username);
    }

    @Override
    public Account getAccountById(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id cannot be negative");
        }

        return accountRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Account is not found!")
        );
    }

    @Override
    public Account getAccountByUsername(String username) {
        if (username == null || username.isBlank() || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        return accountRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("Account with username " + username + " not found")
        );
    }

    @Override
    public Page<Account> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Override
    public Account createAccount(Account account) {
        if (existsByUsername(account.getUsername())) {
            throw new IllegalStateException("Account with username " + account.getUsername() + " already exists");
        }

        account.setPassword(passwordEncoder.encode(account.getPassword()));

        return accountRepository.save(account);
    }

    @Override
    public Account updateAccount(Long accountId, Account account) {
        Account updateAccount = getAccountById(accountId);

        updateAccount.setUsername(account.getUsername());
        updateAccount.setPassword(account.getPassword());
        updateAccount.setUser(account.getUser());
        updateAccount.setRoles(account.getRoles());

        return accountRepository.save(account);
    }

    @Override
    public void deleteAccount(Long id) {
        Account deleteAccount = getAccountById(id);
        accountRepository.delete(deleteAccount);
    }

    @Override
    public RegisterResponse registerAccount(RegisterRequest registerRequest) {
        if (existsByUsername(registerRequest.getUsername())) {
            throw new IllegalStateException("Account with username " + registerRequest.getUsername() + " already exists");
        }

        User user = User.builder()
                .name(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .dateOfBirth(LocalDate.parse(registerRequest.getBirthday()))
                .gender(registerRequest.getGender())
                .build();

        Role userRole = roleRepository.findByName("user")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        Account account = Account.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .user(user)
                .roles(List.of(userRole))
                .build();

        accountRepository.save(account);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        return RegisterResponse.builder()
                .username(registerRequest.getUsername())
                .token(token)
                .expiresIn(JwtConstants.EXPIRATION_TIME)
                .build();
    }

    @Override
    public LoginResponse loginAccount(LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        return LoginResponse.builder()
                .username(loginRequest.getUsername())
                .token(token)
                .expiresIn(JwtConstants.EXPIRATION_TIME)
                .build();
    }

//    @Override
//    public void rateDocument(CreateRatingRequest createRatingRequest) {
//        Account account = accountRepository.findById(accountId).orElseThrow(() ->
//                new ResourceNotFoundException("Account not found")
//        );
//
//
//    }
}
