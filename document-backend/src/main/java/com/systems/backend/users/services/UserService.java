package com.systems.backend.users.services;

import com.systems.backend.users.models.User;
import com.systems.backend.users.resquests.CreateUserRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    Page<User> getAllDocUsers(Pageable pageable);
    User getDocUserById(Long docUserId);
    User createDocUser(CreateUserRequest createUserRequest);
    User updateDocUser(Long docUserId, User user);
    void deleteDocUser(Long docUserId);
}
