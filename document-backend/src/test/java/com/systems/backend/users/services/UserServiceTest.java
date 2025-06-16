package com.systems.backend.users.services;

import com.systems.backend.users.models.User;
import com.systems.backend.users.repositories.UserRepository;
import com.systems.backend.users.resquests.CreateUserRequest;
import com.systems.backend.users.services.impl.UserServiceImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private final Long DOC_USER_ID = 1L;
    private User user;
    private List<User> users;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(DOC_USER_ID)
                .name("testUser")
                .email("test@email.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .gender(true)
                .phone("1234567890")
                .build();

        users = List.of(
                User.builder()
                        .id(1L)
                        .name("user1")
                        .email("user1@email.com")
                        .dateOfBirth(LocalDate.of(1990, 1, 1))
                        .gender(true)
                        .phone("1234567890")
                        .build(),

                User.builder()
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
        Page<User> page = new PageImpl<>(users);
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.getAllDocUsers(pageable);

        assertAll(
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(2, result.getContent().size(), "Should contain all doc users"),
                () -> assertEquals("user1", result.getContent().get(0).getName(), "First user name should match")
        );

        verify(userRepository).findAll(pageable);
    }

    @Test
    void getDocUserById_whenDocUserExists_shouldReturnDocUser() {
        when(userRepository.findById(DOC_USER_ID)).thenReturn(Optional.of(user));

        User result = userService.getDocUserById(DOC_USER_ID);

        assertAll(
                () -> assertNotNull(result, "User should not be null"),
                () -> assertEquals("testUser", result.getName(), "Name should match"),
                () -> assertEquals("test@email.com", result.getEmail(), "Email should match")
        );

        verify(userRepository).findById(DOC_USER_ID);
    }

    @Test
    void getDocUserById_whenDocUserDoesNotExist_shouldReturnNull() {
        when(userRepository.findById(DOC_USER_ID)).thenReturn(Optional.empty());

        User result = userService.getDocUserById(DOC_USER_ID);

        assertNull(result, "Should return null when doc user doesn't exist");
        verify(userRepository).findById(DOC_USER_ID);
    }

    @Test
    void createDocUser() {
        CreateUserRequest request = new CreateUserRequest(
                "testUser",
                "test@email.com",
                "1234567890",
                "1990-01-01",
                true
        );
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createDocUser(request);

        assertAll(
                () -> assertNotNull(result, "Created doc user should not be null"),
                () -> assertEquals("testUser", result.getName(), "Name should match"),
                () -> assertEquals("test@email.com", result.getEmail(), "Email should match"),
                () -> assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth(), "Date of birth should match")
        );

        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateDocUser_whenDocUserExists_shouldUpdateSuccessfully() {
        User updatedDetails = User.builder()
                .id(DOC_USER_ID)
                .name("updatedUser")
                .email("updated@email.com")
                .dateOfBirth(LocalDate.of(1995, 5, 5))
                .gender(false)
                .phone("9876543210")
                .build();
        when(userRepository.findById(DOC_USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedDetails);

        User result = userService.updateDocUser(DOC_USER_ID, updatedDetails);

        assertAll(
                () -> assertNotNull(result, "Updated doc user should not be null"),
                () -> assertEquals("updatedUser", result.getName(), "Name should be updated"),
                () -> assertEquals("updated@email.com", result.getEmail(), "Email should be updated")
        );

        verify(userRepository).findById(DOC_USER_ID);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateDocUser_whenDocUserDoesNotExist_shouldThrowResourceNotFoundException() {
        User updatedDetails = new User();
        when(userRepository.findById(DOC_USER_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateDocUser(DOC_USER_ID, updatedDetails);
        });

        assertEquals("Doc User Not Found", thrown.getMessage());
        verify(userRepository).findById(DOC_USER_ID);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteDocUser_whenDocUserExists_shouldDeleteSuccessfully() {
        when(userRepository.findById(DOC_USER_ID)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteDocUser(DOC_USER_ID);

        verify(userRepository).findById(DOC_USER_ID);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteDocUser_whenDocUserDoesNotExist_shouldThrowRuntimeException() {
        when(userRepository.findById(DOC_USER_ID)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userService.deleteDocUser(DOC_USER_ID);
        });

        assertEquals("Role is not found!", thrown.getMessage());
        verify(userRepository).findById(DOC_USER_ID);
        verify(userRepository, never()).delete(any(User.class));
    }
}