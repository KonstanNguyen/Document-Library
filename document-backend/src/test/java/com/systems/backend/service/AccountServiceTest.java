package com.systems.backend.service;

import com.systems.backend.model.Account;
import com.systems.backend.model.Role;
import com.systems.backend.repository.AccountRepository;
import com.systems.backend.repository.RoleRepository;
import com.systems.backend.requests.LoginRequest;
import com.systems.backend.requests.RegisterRequest;
import com.systems.backend.responses.LoginResponse;
import com.systems.backend.responses.RegisterResponse;
import com.systems.backend.security.JwtGenerator;
import com.systems.backend.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    private final long ACCOUNT_ID = 1L;
    private final String ACCOUNT_NAME = "test";
    private final List<Account> accounts = List.of(
            new Account(1L, "account1", "password1", null, null, null, null),
            new Account(2L, "account2", "password2", null, null, null, null),
            new Account(3L, "account3", "password3", null, null, null, null),
            new Account(4L, "account4", "password4", null, null, null, null)
    );
    private Account account;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtGenerator jwtGenerator;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        account = new Account(ACCOUNT_ID, "account1", "password1", null, null, null, null);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void existsByUsername_whenUsernameExists_ShouldReturnTrue() {
        when(accountRepository.existsByUsername(ACCOUNT_NAME)).thenReturn(true);

        assertTrue(accountService.existsByUsername(ACCOUNT_NAME), "Should be returned true");

        verify(accountRepository).existsByUsername(ACCOUNT_NAME);
    }

    @Test
    void existsByUsername_whenUsernameDoesNotExist_ShouldReturnFalse() {
        String nonExistingUsername = "unknownUser";
        when(accountRepository.existsByUsername(nonExistingUsername)).thenReturn(false);

        assertFalse(accountService.existsByUsername(nonExistingUsername), "Should be returned false");

        verify(accountRepository).existsByUsername(nonExistingUsername);
    }

    @Test
    void existsByUsername_whenUsernameIsInvalid_ShouldThrowIllegalArgumentException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> accountService.existsByUsername(null), "Exception should be thrown"),
                () -> assertThrows(IllegalArgumentException.class, () -> accountService.existsByUsername(""), "Exception should be thrown"),
                () -> assertThrows(IllegalArgumentException.class, () -> accountService.existsByUsername("    "), "Exception should be thrown")
        );
    }

    @Test
    void getAccountById_whenAccountExist_ShouldReturnAccount() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        Account result = accountService.getAccountById(ACCOUNT_ID);

        assertAll(
                () -> assertNotNull(result, "The account should not be null"),
                () -> assertEquals("account1", result.getUsername(), "The account username should be match"),
                () -> assertEquals("password1", result.getPassword(), "The account password should be match")
        );

        verify(accountRepository).findById(ACCOUNT_ID);
    }

    @Test
    void getAccountById_whenAccountNotFound_ShouldThrowResourceNotFoundException() {
        long nonExistingAccountId = 10L;
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAccountById(nonExistingAccountId);
        });

        assertEquals("Account is not found!", thrown.getMessage(), "Exception message should be match");

        verify(accountRepository).findById(nonExistingAccountId);
    }

    @Test
    void getAccountByUsername_whenUsernameExists_ShouldReturnAccount() {
        String existingUsername = "account1";
        when(accountRepository.findByUsername(existingUsername)).thenReturn(Optional.of(account));

        Account result = accountService.getAccountByUsername(existingUsername);

        assertAll(
                () -> assertNotNull(result, "The account should not be null"),
                () -> assertEquals("password1", result.getPassword(), "The account password should be match")
        );

        verify(accountRepository).findByUsername(existingUsername);
    }

    @Test
    void getAccountByUsername_whenUsernameDoesNotExist_ShouldThrowResourceNotFoundException() {
        String nonExistingUsername = "unknownUser";
        when(accountRepository.findByUsername(nonExistingUsername)).thenThrow(
                new ResourceNotFoundException("Account with username " + nonExistingUsername + " not found")
        );

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAccountByUsername(nonExistingUsername);
        });

        assertEquals(
                "Account with username " + nonExistingUsername + " not found",
                thrown.getMessage(),
                "Exception message should be match"
        );
    }

    @Test
    void getAccountByUsername_whenUsernameIsInvalid_ShouldThrowIllegalArgumentException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> accountService.getAccountByUsername(null), "Exception should be thrown"),
                () -> assertThrows(IllegalArgumentException.class, () -> accountService.getAccountByUsername(""), "Exception should be thrown"),
                () -> assertThrows(IllegalArgumentException.class, () -> accountService.getAccountByUsername("      "), "Exception should be thrown")
        );
    }

    @Test
    void getAllAccounts() {
        Page<Account> accounts = new PageImpl<>(List.of(account));
        when(accountRepository.findAll(PageRequest.of(0, 10))).thenReturn(accounts);
        Page<Account> result = accountService.getAllAccounts(PageRequest.of(0, 10));
        assertEquals(1, result.getContent().size(), "Should return all accounts.");
        verify(accountRepository).findAll(PageRequest.of(0, 10));
    }

    @Test
    void createAccount() {
        when(passwordEncoder.encode(account.getPassword())).thenReturn("encodedPassword");
        when(accountRepository.save(any())).thenReturn(account);
        Account result = accountService.createAccount(account);
        assertEquals("encodedPassword", result.getPassword(), "Password should be encoded");
        verify(accountRepository).save(any());
    }

    @Test
    void updateAccount() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);
        Account result = accountService.updateAccount(ACCOUNT_ID, account);
        assertNotNull(result, "Expected non-null updated account.");
        verify(accountRepository).save(any());
    }

    @Test
    void deleteAccount() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        doNothing().when(accountRepository).delete(account);
        accountService.deleteAccount(ACCOUNT_ID);
        verify(accountRepository).delete(account);
    }

    @Test
    void registerAccount() {
        RegisterRequest registerRequest = new RegisterRequest("ntn", "email", "0123456789", "2000-01-01", true, "username", "password");
        Role role = new Role(1L, "user", null, null);
        when(roleRepository.findByName("user")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(accountRepository.save(any())).thenReturn(account);
        when(jwtGenerator.generateToken(any())).thenReturn("mockToken");
        RegisterResponse response = accountService.registerAccount(registerRequest);
        assertEquals("mockToken", response.getToken(), "Token should match");
    }

    @Test
    void loginAccount() {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken("username", "password"));
        when(jwtGenerator.generateToken(any())).thenReturn("mockToken");
        LoginResponse response = accountService.loginAccount(loginRequest);
        assertEquals("mockToken", response.getToken(), "Token should match");
    }
}