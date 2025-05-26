package com.systems.backend.users.services;

import com.systems.backend.users.models.DocUser;
import com.systems.backend.users.resquests.CreateDocUserRequest;

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
