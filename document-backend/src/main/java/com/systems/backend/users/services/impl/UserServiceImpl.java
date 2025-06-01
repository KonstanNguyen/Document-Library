package com.systems.backend.users.services.impl;

import com.systems.backend.users.models.User;
import com.systems.backend.users.repositories.UserRepository;
import com.systems.backend.users.resquests.CreateUserRequest;
import com.systems.backend.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<User> getAllDocUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getDocUserById(Long docUserId) {
        Optional<User> docUser = userRepository.findById(docUserId);
        return docUser.orElse(null);
    }

    @Override
    public User createDocUser(CreateUserRequest createUserRequest) {
        User user = new User();

        user.setName(createUserRequest.getName());
        user.setEmail(createUserRequest.getEmail());
        user.setDateOfBirth(LocalDate.parse(createUserRequest.getBirthday()));
        user.setGender(createUserRequest.getGender());
        user.setPhone(createUserRequest.getPhone());

        return userRepository.save(user);
    }

    @Override
    public User updateDocUser(Long docUserId, User user) {
        User updateUser = userRepository.findById(docUserId).orElseThrow(() ->
                new ResourceNotFoundException("Doc User Not Found")
        );

        updateUser.setId(docUserId);

        return userRepository.save(updateUser);
    }

    @Override
    public void deleteDocUser(Long docUserId) {
        User role = getDocUserById(docUserId);
        if (role == null) {
            throw new RuntimeException("Role is not found!");
        }
        userRepository.delete(role);
    }
}
