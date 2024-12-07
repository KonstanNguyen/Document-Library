package com.systems.backend.controller;

import com.systems.backend.mapper.DocUserMapper;
import com.systems.backend.mapper.DocumentMapper;
import com.systems.backend.model.DocUser;
import com.systems.backend.model.Document;
import com.systems.backend.requests.CreateDocUserRequest;
import com.systems.backend.requests.PaginationRequest;
import com.systems.backend.responses.DocUserResponse;
import com.systems.backend.responses.DocumentResponse;
import com.systems.backend.service.DocUserService;
import com.systems.backend.service.DocumentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/doc-users")
public class DocUserController {
    @Autowired
    private DocUserService docUserService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocUserMapper docUserMapper;

    @Autowired
    private DocumentMapper documentMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Page<DocUserResponse> getAllDocUsers( @RequestBody(required = false) PaginationRequest pageRequest) {
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

        Page<DocUser> docUserPage = docUserService.getAllDocUsers(pageable);
        
        return docUserMapper.toDTOPage(docUserPage);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public DocUser createDocUser(@RequestBody CreateDocUserRequest createDocUserRequest) {
        return docUserService.createDocUser(createDocUserRequest);
    }

    @GetMapping("{docUserId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DocUserResponse getDocUser(@PathVariable(name = "docUserId") Long docUserId) {
        DocUser docUser = docUserService.getDocUserById(docUserId);
        return docUserMapper.toDTO(docUser);
    }

    @RequestMapping(value = "{docUserId}/update", method = {RequestMethod.PUT, RequestMethod.POST, RequestMethod.PATCH})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DocUser updateDocUser(@PathVariable(name = "docUserId") Long docUserId, @RequestBody DocUser docUser) {
        return docUserService.updateDocUser(docUserId, docUser);
    }

    @DeleteMapping("{docUserId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocUser(@PathVariable(name = "docUserId") Long accountId) {
        docUserService.deleteDocUser(accountId);
    }


    @GetMapping("{docUserId}/documents")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
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
        DocUser docUser = docUserService.getDocUserById(docuserId);
        if (docUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "DocUser not found");
        }

        Page<Document> documentPage = documentService.getDocumentsByAuthor(docUser, pageable);
        
        return documentMapper.toDTOPage(documentPage);
    }
}
