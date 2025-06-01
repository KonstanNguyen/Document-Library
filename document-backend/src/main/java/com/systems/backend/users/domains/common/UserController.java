package com.systems.backend.users.domains.common;

import com.systems.backend.users.mappers.UserMapper;
import com.systems.backend.documents.mappers.DocumentMapper;
import com.systems.backend.users.models.User;
import com.systems.backend.documents.models.Document;
import com.systems.backend.users.resquests.CreateUserRequest;
import com.systems.backend.common.requests.PaginationRequest;
import com.systems.backend.users.responses.UserResponse;
import com.systems.backend.documents.responses.DocumentResponse;
import com.systems.backend.users.services.UserService;
import com.systems.backend.documents.services.DocumentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/doc-users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DocumentMapper documentMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponse> getAllDocUsers(@RequestBody(required = false) PaginationRequest pageRequest) {
        Pageable pageable;
        if (pageRequest == null) {
            pageable = PageRequest.of(0, 6, Sort.by("id").ascending());
        } else {
            int page = pageRequest.getPage() > 0 ? pageRequest.getPage() : 0;
            int size = pageRequest.getSize() > 1 ? pageRequest.getSize() : 6;
            String sortBy = pageRequest.getSortBy() != null ? pageRequest.getSortBy() : "id";
            String sortDir = pageRequest.getSortDirection() != null ? pageRequest.getSortDirection() : "asc";

            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            pageable = PageRequest.of(page, size, sort);
        }

        Page<User> docUserPage = userService.getAllDocUsers(pageable);
        
        return userMapper.toDTOPage(docUserPage);
    }

    @PreAuthorize("hasAnyAuthority('admin') or hasAnyAuthority('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createDocUser(@RequestBody CreateUserRequest createUserRequest) {
        return userService.createDocUser(createUserRequest);
    }

    @GetMapping("{docUserId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getDocUser(@PathVariable(name = "docUserId") Long docUserId) {
        User user = userService.getDocUserById(docUserId);
        return userMapper.toDTO(user);
    }

    @RequestMapping(value = "{docUserId}/update", method = {RequestMethod.PUT, RequestMethod.POST, RequestMethod.PATCH})
    @ResponseStatus(HttpStatus.OK)
    public User updateDocUser(@PathVariable(name = "docUserId") Long docUserId, @RequestBody User user) {
        return userService.updateDocUser(docUserId, user);
    }

    @PreAuthorize("hasAnyAuthority('admin') or hasAnyAuthority('ADMIN')")
    @DeleteMapping("{docUserId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocUser(@PathVariable(name = "docUserId") Long accountId) {
        userService.deleteDocUser(accountId);
    }


    @GetMapping("{docUserId}/documents")
    @ResponseStatus(HttpStatus.OK)
    public Page<DocumentResponse> getDocumentsByAuthor(
        @PathVariable(name ="docUserId") Long docuserId,
        @RequestBody(required = false) PaginationRequest pageRequest) {
        Pageable pageable;
        if (pageRequest == null) {
            pageable = PageRequest.of(0, 6, Sort.by("createAt").descending());
        } else {
            int page = pageRequest.getPage() > 0 ? pageRequest.getPage() : 0;
            int size = pageRequest.getSize() > 1 ? pageRequest.getSize() : 6;
            String sortBy = pageRequest.getSortBy() != null ? pageRequest.getSortBy() : "createAt";
            String sortDir = pageRequest.getSortDirection() != null ? pageRequest.getSortDirection() : "desc";

            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            pageable = PageRequest.of(page, size, sort);
        }
        User user = userService.getDocUserById(docuserId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "DocUser not found");
        }

        Page<Document> documentPage = documentService.getDocumentsByAuthor(user, pageable);
        
        return documentMapper.toDTOPage(documentPage);
    }
}
