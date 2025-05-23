package com.systems.backend.controller;

import com.systems.backend.mapper.AccountMapper;
import com.systems.backend.model.Account;
import com.systems.backend.model.Document;
import com.systems.backend.model.Role;
import com.systems.backend.requests.CreateRatingRequest;
import com.systems.backend.requests.LoginRequest;
import com.systems.backend.requests.PaginationRequest;
import com.systems.backend.requests.RegisterRequest;
import com.systems.backend.responses.AccountResponse;
import com.systems.backend.responses.ApiResponse;
import com.systems.backend.responses.HistoryDownloadResponse;
import com.systems.backend.responses.RatingResponse;
import com.systems.backend.service.*;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private HistoryDownloadService historyDownloadService;

    @Autowired
    private RatingService ratingService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<AccountResponse> getAllAccounts(@RequestBody(required = false) PaginationRequest pageRequest) {
        Pageable pageable;
        if (pageRequest == null) {
            pageable = PageRequest.of(0, 6, Sort.by("username").ascending());
        } else {
            int page = Math.max(pageRequest.getPage(), 0);
            int size = pageRequest.getSize() > 0 ? pageRequest.getSize() : 10;
            String sortBy = pageRequest.getSortBy() != null ? pageRequest.getSortBy() : "username";
            String sortDir = pageRequest.getSortDirection() != null ? pageRequest.getSortDirection() : "asc";

            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            pageable = PageRequest.of(page, size, sort);
        }

        Page<Account> accountPage = accountService.getAllAccounts(pageable);
        return accountMapper.toDTOPage(accountPage);
    }

    @PreAuthorize("hasAnyAuthority('admin') or hasAnyAuthority('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    @GetMapping("{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public AccountResponse getAccount(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
        return accountMapper.toDTO(account);
    }

    @RequestMapping(value = "{accountId}/update", method = { RequestMethod.PUT, RequestMethod.POST,
            RequestMethod.PATCH })
    @ResponseStatus(HttpStatus.OK)
    public Account updateAccount(
            @PathVariable Long accountId,
            @RequestBody Account account) {
        return accountService.updateAccount(accountId, account);
    }

    @PreAuthorize("hasAnyAuthority('admin') or hasAnyAuthority('ADMIN')")
    @DeleteMapping("{accountId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
    }

    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> loginAccount(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.builder()
                .data(accountService.loginAccount(loginRequest))
                .message("Login successful")
                .code(HttpStatus.OK.value())
                .build();
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> registerAccount(@RequestBody RegisterRequest registerRequest) {
        return ApiResponse.builder()
                .data(accountService.registerAccount(registerRequest))
                .message("Account successfully registered!")
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("getUserIdByUsername/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> getUserIdByUsername(@PathVariable String username) {
        Account account = accountService.getAccountByUsername(username);

        return ApiResponse.builder()
                .data(account.getId())
                .message("User ID fetched successfully")
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("getRoleByAccountId/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Object> getRoleById(@PathVariable("accountId") Long accountId) {
        List<Role> role = roleService.getRolesByAccountId(accountId);

        return ApiResponse.builder()
                .data(role)
                .message("Role fetched successfully")
                .code(HttpStatus.OK.value())
                .build();
    }

    @GetMapping("{accountId}/history-download")
    @ResponseStatus(HttpStatus.OK)
    public List<HistoryDownloadResponse> getHistoryByAccountId(@PathVariable Long accountId) {
        return historyDownloadService.getHistoryByAccountId(accountId);
    }

    @PostMapping("rate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RatingResponse rateAccount(@Valid @RequestBody CreateRatingRequest request) {
        return ratingService.createRating(request);
    }
}
