package com.systems.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systems.backend.documents.services.DocumentService;
import com.systems.backend.download.responses.HistoryDownloadResponse;
import com.systems.backend.download.services.HistoryDownloadService;
import com.systems.backend.ratings.responses.RatingResponse;
import com.systems.backend.ratings.services.RatingService;
import com.systems.backend.users.domains.common.AccountController;
import com.systems.backend.users.mappers.AccountMapper;
import com.systems.backend.users.models.Account;
import com.systems.backend.users.models.Role;
import com.systems.backend.ratings.resquests.CreateRatingRequest;
import com.systems.backend.users.responses.AccountResponse;
import com.systems.backend.users.responses.LoginResponse;
import com.systems.backend.users.responses.RegisterResponse;
import com.systems.backend.users.resquests.LoginRequest;
import com.systems.backend.users.resquests.RegisterRequest;
import com.systems.backend.users.services.AccountService;
import com.systems.backend.users.services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AccountService accountService;

        @MockitoBean
        private DocumentService documentService; // Added missing dependency

        @MockitoBean
        private RoleService roleService;

        @MockitoBean
        private AccountMapper accountMapper;

        @MockitoBean
        private HistoryDownloadService historyDownloadService;

        @MockitoBean
        private RatingService ratingService;

        private Account account;
        private AccountResponse accountResponse;
        private final Long accountId = 1L;
        private List<Role> roles;
        private List<HistoryDownloadResponse> historyDownloads;
        private RatingResponse ratingResponse;
        private RegisterResponse registerResponse;
        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                account = new Account(accountId, "testuser", "password", null, null, null, null);
                accountResponse = new AccountResponse();
                roles = List.of(new Role(1L, "user", "User role", null));
                historyDownloads = List.of(new HistoryDownloadResponse());
                ratingResponse = new RatingResponse();
                registerResponse = RegisterResponse.builder()
                                .username("testuser")
                                .token("mock-token")
                                .expiresIn(3600)
                                .build();
                objectMapper = new ObjectMapper();
        }

        // User endpoints
        @Test
        @WithMockUser
        void getAllAccounts_ShouldReturnPagedAccounts() throws Exception {
                Pageable pageable = PageRequest.of(0, 6, Sort.by("username").ascending());
                List<Account> accountList = List.of(account);
                Page<Account> accountPage = new PageImpl<>(accountList, pageable, 1);
                Page<AccountResponse> responsePage = new PageImpl<>(List.of(accountResponse));

                when(accountService.getAllAccounts(any(Pageable.class))).thenReturn(accountPage);
                when(accountMapper.toDTOPage(accountPage)).thenReturn(responsePage);

                mockMvc.perform(get("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content", hasSize(1)));

                verify(accountService).getAllAccounts(any(Pageable.class));
                verify(accountMapper).toDTOPage(accountPage);
        }

        @Test
        @WithMockUser
        void getAccount_ShouldReturnAccount() throws Exception {
                when(accountService.getAccountById(accountId)).thenReturn(account);
                when(accountMapper.toDTO(account)).thenReturn(accountResponse);

                mockMvc.perform(get("/api/accounts/{accountId}", accountId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());

                verify(accountService).getAccountById(accountId);
                verify(accountMapper).toDTO(account);
        }

        @Test
        @WithMockUser
        void updateAccount_ShouldReturnUpdatedAccount() throws Exception {
                when(accountService.updateAccount(eq(accountId), any(Account.class))).thenReturn(account);

                mockMvc.perform(put("/api/accounts/{accountId}/update", accountId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(account)))
                                .andExpect(status().isOk());

                verify(accountService).updateAccount(eq(accountId), any(Account.class));
        }

        @Test
        @WithMockUser
        void loginAccount_ShouldReturnSuccessResponse() throws Exception {
                LoginRequest loginRequest = new LoginRequest("testuser", "password");
                LoginResponse loginResponse = LoginResponse.builder()
                                .username("testuser")
                                .token("mock-token")
                                .expiresIn(3600)
                                .build();
                when(accountService.loginAccount(any(LoginRequest.class))).thenReturn(loginResponse);

                MvcResult result = mockMvc.perform(post("/api/accounts/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Login successful"))
                                .andExpect(jsonPath("$.data.token").value("mock-token"))
                                .andReturn();

                String responseJson = result.getResponse().getContentAsString();
                System.out.println("Login Test Passed. Response JSON:\n" +
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                                objectMapper.readTree(responseJson)));
                verify(accountService).loginAccount(any(LoginRequest.class));
        }

        @Test
        @WithMockUser
        void registerAccount_ShouldReturnSuccessResponse() throws Exception {
                RegisterRequest registerRequest = new RegisterRequest(
                                "Test User", "email@example.com", "1234567890", "1990-01-01", true,
                                "testuser", "password");
                when(accountService.registerAccount(any(RegisterRequest.class))).thenReturn(registerResponse);

                MvcResult result = mockMvc.perform(post("/api/accounts/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Account successfully registered!"))
                                .andExpect(jsonPath("$.data.username").value("testuser"))
                                .andExpect(jsonPath("$.data.token").value("mock-token"))
                                .andExpect(jsonPath("$.data.expiresIn").value(3600))
                                .andReturn();
                String responseJson = result.getResponse().getContentAsString();
                System.out.println("Register Test Passed. Response JSON:\n" +
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                                                objectMapper.readTree(responseJson)));

                verify(accountService).registerAccount(any(RegisterRequest.class));
        }

        @Test
        @WithMockUser
        void registerAccount_WithInvalidData_ShouldReturnBadRequest() throws Exception {
                RegisterRequest invalidRequest = new RegisterRequest(
                                "", "", "", "", null,
                                "", "");

                mockMvc.perform(post("/api/accounts/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());

                verify(accountService, never()).registerAccount(any(RegisterRequest.class));
        }

        @Test
        @WithMockUser
        void getUserIdByUsername_ShouldReturnUserId() throws Exception {
                when(accountService.getAccountByUsername("testuser")).thenReturn(account);

                mockMvc.perform(get("/api/accounts/getUserIdByUsername/testuser")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data").value(accountId));

                verify(accountService).getAccountByUsername("testuser");
        }

        @Test
        @WithMockUser
        void getRoleByAccountId_ShouldReturnRoles() throws Exception {
                when(roleService.getRolesByAccountId(accountId)).thenReturn(roles);

                mockMvc.perform(get("/api/accounts/getRoleByAccountId/{accountId}", accountId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data", hasSize(1)));

                verify(roleService).getRolesByAccountId(accountId);
        }

        @Test
        @WithMockUser
        void getHistoryByAccountId_ShouldReturnHistory() throws Exception {
                when(historyDownloadService.getHistoryByAccountId(accountId)).thenReturn(historyDownloads);

                mockMvc.perform(get("/api/accounts/{accountId}/history-download", accountId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)));

                verify(historyDownloadService).getHistoryByAccountId(accountId);
        }

        @Test
        @WithMockUser
        void rateAccount_ShouldReturnRatingResponse() throws Exception {
                CreateRatingRequest ratingRequest = new CreateRatingRequest(accountId, 1L, 5);
                when(ratingService.createRating(any(CreateRatingRequest.class))).thenReturn(ratingResponse);

                mockMvc.perform(post("/api/accounts/rate")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(ratingRequest)))
                                .andExpect(status().isAccepted());

                verify(ratingService).createRating(any(CreateRatingRequest.class));
        }

        // Admin endpoints
        @Test
        @WithMockUser(authorities = { "admin" })
        void createAccount_ShouldReturnCreatedAccount() throws Exception {
                when(accountService.createAccount(any(Account.class))).thenReturn(account);

                mockMvc.perform(post("/api/accounts")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(account)))
                                .andExpect(status().isCreated());

                verify(accountService).createAccount(any(Account.class));
        }

        @Test
        @WithMockUser(authorities = { "admin" })
        void deleteAccount_ShouldReturn204() throws Exception {
                mockMvc.perform(delete("/api/accounts/{accountId}/delete", accountId)
                                .with(csrf()))
                                .andExpect(status().isNoContent());

                verify(accountService).deleteAccount(accountId);
        }

        @Test
        @WithMockUser(authorities = { "admin" })
        void deleteAccount_WhenNotFound_ShouldReturn404() throws Exception {
                doThrow(new ResourceNotFoundException("Account not found"))
                                .when(accountService).deleteAccount(accountId);

                mockMvc.perform(delete("/api/accounts/{accountId}/delete", accountId)
                                .with(csrf()))
                                .andExpect(status().isNotFound());

                verify(accountService).deleteAccount(accountId);
        }

        @Test
        @WithMockUser(authorities = { "user" })
        void createAccount_WhenNotAdmin_ShouldReturn403() throws Exception {
                mockMvc.perform(post("/api/accounts")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(account)))
                                .andExpect(status().isForbidden());

                verify(accountService, never()).createAccount(any(Account.class));
        }
}