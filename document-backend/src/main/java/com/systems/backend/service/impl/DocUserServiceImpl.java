package com.systems.backend.service.impl;

import com.systems.backend.model.DocUser;
import com.systems.backend.repository.DocUserRepository;
import com.systems.backend.requests.CreateDocUserRequest;
import com.systems.backend.service.DocUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DocUserServiceImpl implements DocUserService {
    @Autowired
    private DocUserRepository docUserRepository;

    @Override
    public Page<DocUser> getAllDocUsers(Pageable pageable) {
        return docUserRepository.findAll(pageable);
    }

    @Override
    public DocUser getDocUserById(Long docUserId) {
        Optional<DocUser> docUser = docUserRepository.findById(docUserId);
        return docUser.orElse(null);
    }

    @Override
    public DocUser createDocUser(CreateDocUserRequest createDocUserRequest) {
        DocUser docUser = new DocUser();

        docUser.setName(createDocUserRequest.getName());
        docUser.setEmail(createDocUserRequest.getEmail());
        docUser.setDateOfBirth(LocalDate.parse(createDocUserRequest.getBirthday()));
        docUser.setGender(createDocUserRequest.getGender());
        docUser.setPhone(createDocUserRequest.getPhone());

        return docUserRepository.save(docUser);
    }

    @Override
    public DocUser updateDocUser(Long docUserId, DocUser docUser) {
        DocUser updateDocUser = docUserRepository.findById(docUserId).orElseThrow(() ->
                new ResourceNotFoundException("Doc User Not Found")
        );

        updateDocUser.setId(docUserId);

        return docUserRepository.save(updateDocUser);
    }

    @Override
    public void deleteDocUser(Long docUserId) {
        DocUser role = getDocUserById(docUserId);
        if (role == null) {
            throw new RuntimeException("Role is not found!");
        }
        docUserRepository.delete(role);
    }
}
