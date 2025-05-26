package com.systems.backend.repository;

import com.systems.backend.users.models.Role;
import com.systems.backend.users.repositories.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class RoleRepositoryTest {
    private final List<Role> roles = List.of(
            new Role(null, "admin", "Admin description", null),
            new Role(null, "user", "User description", null),
            new Role(null, "guest", "Guest description", null)
    );

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roles.forEach(entityManager::persistAndFlush);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    void findByName_ShouldReturnRole_WhenRoleExists() {
        // Look for a valid role that exists in the database, such as "admin"
        Optional<Role> foundRole = roleRepository.findByName("admin");
        assertTrue(foundRole.isPresent(), "Role 'admin' should exist in the database!");
        assertEquals("admin", foundRole.get().getName());
    }

    @Test
    void existsByName_ShouldReturnTrue_WhenRoleExists() {
        // Check if "admin" role exists in the database
        boolean exists = roleRepository.existsByName("admin");
        assertTrue(exists, "Role 'admin' should exist in the database!");
    }

    @Test
    void existsByName_ShouldReturnFalse_WhenRoleDoesNotExist() {
        // Check for a role that doesn't exist, like "nonexistentrole"
        boolean exists = roleRepository.existsByName("nonexistentrole");
        assertFalse(exists, "Role 'nonexistentrole' should not exist in the database!");
    }
}