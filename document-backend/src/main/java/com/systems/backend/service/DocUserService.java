package com.systems.backend.service;

import com.systems.backend.model.DocUser;
import com.systems.backend.requests.CreateDocUserRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface DocUserService {
    Page<DocUser> getAllDocUsers(Pageable pageable);
    DocUser getDocUserById(Long docUserId);
    DocUser createDocUser(CreateDocUserRequest createDocUserRequest);
    DocUser updateDocUser(Long docUserId, DocUser docUser);
    void deleteDocUser(Long docUserId);
}
