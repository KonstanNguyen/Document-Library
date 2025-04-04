package com.systems.backend.service;

import com.systems.backend.model.DocUser;
import com.systems.backend.repository.DocUserRepository;
import com.systems.backend.requests.CreateDocUserRequest;
import com.systems.backend.service.impl.DocUserServiceImpl;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocUserServiceTest {
    private final Long DOC_USER_ID = 1L;
    private DocUser docUser;
    private List<DocUser> docUsers;

    @Mock
    private DocUserRepository docUserRepository;

    @InjectMocks
    private DocUserServiceImpl docUserService;

    @BeforeEach
    void setUp() {
        docUser = DocUser.builder()
                .id(DOC_USER_ID)
                .name("testUser")
                .email("test@email.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(true)
                .phone("1234567890")
                .build();

        docUsers = List.of(
                DocUser.builder()
                        .id(1L)
                        .name("user1")
                        .email("user1@email.com")
                        .dateOfBirth(LocalDate.of(1990, 1, 1))
                        .gender(true)
                        .phone("1234567890")
                        .build(),

                DocUser.builder()
                        .id(2L)
                        .name("user2")
                        .email("user2@email.com")
                        .dateOfBirth(LocalDate.of(1990, 2, 2))
                        .gender(false)
                        .phone("0987654321")
                        .build()
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllDocUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DocUser> page = new PageImpl<>(docUsers);
        when(docUserRepository.findAll(pageable)).thenReturn(page);

        Page<DocUser> result = docUserService.getAllDocUsers(pageable);

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(2, result.getContent().size(), "Should contain all doc users"),
                () -> assertEquals("user1", result.getContent().get(0).getName(), "First user name should match")
        );

        verify(docUserRepository).findAll(pageable);
    }

    @Test
    void getDocUserById_whenDocUserExists_shouldReturnDocUser() {
        when(docUserRepository.findById(DOC_USER_ID)).thenReturn(Optional.of(docUser));

        DocUser result = docUserService.getDocUserById(DOC_USER_ID);

        assertAll(
                () -> assertNotNull(result, "DocUser should not be null"),
                () -> assertEquals("testUser", result.getName(), "Name should match"),
                () -> assertEquals("test@email.com", result.getEmail(), "Email should match")
        );

        verify(docUserRepository).findById(DOC_USER_ID);
    }

    @Test
    void getDocUserById_whenDocUserDoesNotExist_shouldReturnNull() {
        when(docUserRepository.findById(DOC_USER_ID)).thenReturn(Optional.empty());

        DocUser result = docUserService.getDocUserById(DOC_USER_ID);

        assertNull(result, "Should return null when doc user doesn't exist");
        verify(docUserRepository).findById(DOC_USER_ID);
    }

    @Test
    void createDocUser() {
        CreateDocUserRequest request = new CreateDocUserRequest(
                "testUser",
                "test@email.com",
                "1234567890",
                "1990-01-01",
                true
        );
        when(docUserRepository.save(any(DocUser.class))).thenReturn(docUser);

        DocUser result = docUserService.createDocUser(request);

        assertAll(
                () -> assertNotNull(result, "Created doc user should not be null"),
                () -> assertEquals("testUser", result.getName(), "Name should match"),
                () -> assertEquals("test@email.com", result.getEmail(), "Email should match"),
                () -> assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth(), "Date of birth should match")
        );

        verify(docUserRepository).save(any(DocUser.class));
    }

    @Test
    void updateDocUser_whenDocUserExists_shouldUpdateSuccessfully() {
        DocUser updatedDetails = DocUser.builder()
                .id(DOC_USER_ID)
                .name("updatedUser")
                .email("updated@email.com")
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .gender(false)
                .phone("9876543210")
                .build();
        when(docUserRepository.findById(DOC_USER_ID)).thenReturn(Optional.of(docUser));
        when(docUserRepository.save(any(DocUser.class))).thenReturn(updatedDetails);

        DocUser result = docUserService.updateDocUser(DOC_USER_ID, updatedDetails);

        assertAll(
                () -> assertNotNull(result, "Updated doc user should not be null"),
                () -> assertEquals("updatedUser", result.getName(), "Name should be updated"),
                () -> assertEquals("updated@email.com", result.getEmail(), "Email should be updated")
        );

        verify(docUserRepository).findById(DOC_USER_ID);
        verify(docUserRepository).save(any(DocUser.class));
    }

    @Test
    void updateDocUser_whenDocUserDoesNotExist_shouldThrowResourceNotFoundException() {
        DocUser updatedDetails = new DocUser();
        when(docUserRepository.findById(DOC_USER_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            docUserService.updateDocUser(DOC_USER_ID, updatedDetails);
        });

        assertEquals("Doc User Not Found", thrown.getMessage());
        verify(docUserRepository).findById(DOC_USER_ID);
        verify(docUserRepository, never()).save(any(DocUser.class));
    }

    @Test
    void deleteDocUser_whenDocUserExists_shouldDeleteSuccessfully() {
        when(docUserRepository.findById(DOC_USER_ID)).thenReturn(Optional.of(docUser));
        doNothing().when(docUserRepository).delete(docUser);

        docUserService.deleteDocUser(DOC_USER_ID);

        verify(docUserRepository).findById(DOC_USER_ID);
        verify(docUserRepository).delete(docUser);
    }

    @Test
    void deleteDocUser_whenDocUserDoesNotExist_shouldThrowRuntimeException() {
        when(docUserRepository.findById(DOC_USER_ID)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            docUserService.deleteDocUser(DOC_USER_ID);
        });

        assertEquals("Role is not found!", thrown.getMessage());
        verify(docUserRepository).findById(DOC_USER_ID);
        verify(docUserRepository, never()).delete(any(DocUser.class));
    }
}