package com.systems.backend.service;

import com.systems.backend.model.Role;
import com.systems.backend.repository.RoleRepository;
import com.systems.backend.requests.CreateRoleRequest;
import com.systems.backend.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;

    @BeforeEach
    void setUp() {
        // Set up a mock role
        role = new Role(1L, "admin", "Admin role", null);
    }

    @Test
    void getAllRoles() {
        // Set up mock behavior
        when(roleRepository.findAll()).thenReturn(List.of(role));

        // Call the method under test
        var roles = roleService.getAllRoles();

        // Assertions
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("admin", roles.get(0).getName());

        // Verify interaction with the mock
        verify(roleRepository).findAll();
    }

    @Test
    void getRoleById() {
        // Set up mock behavior
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        // Call the method under test
        Role foundRole = roleService.getRoleById(1L);

        // Assertions
        assertEquals("admin", foundRole.getName());

        // Verify interaction with the mock
        verify(roleRepository).findById(1L);
    }

    @Test
    void findByName() {
        // Set up mock behavior
        when(roleRepository.findByName("admin")).thenReturn(Optional.of(role));

        // Call the method under test
        Optional<Role> foundRole = Optional.ofNullable(roleService.findByName("admin"));

        // Assertions
        assertTrue(foundRole.isPresent());
        assertEquals("admin", foundRole.get().getName());

        // Verify interaction with the mock
        verify(roleRepository).findByName("admin");
    }

    @Test
    void createRole() {
        // Prepare input CreateRoleRequest
        CreateRoleRequest createRoleRequest = new CreateRoleRequest("admin", null);

        // Set up mock behavior for saving the role
        Role roleToCreate = new Role(null, "admin", null, null); // Role object to be saved
        when(roleRepository.save(any(Role.class))).thenReturn(roleToCreate);

        // Call the method under test
        Role createdRole = roleService.createRole(createRoleRequest);

        // Assertions
        assertNotNull(createdRole);
        assertEquals("admin", createdRole.getName());
        assertNull(createdRole.getDescription());  // Since description is null

        // Verify interaction with the mock
        verify(roleRepository).save(any(Role.class)); // Ensure save was called with a Role
    }

//    @Test
//    void updateRole() {
//        // Set up mock behavior for updating the role
//        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
//        when(roleRepository.save(role)).thenReturn(role);
//
//        // Update role
//        role.setName("admin_updated");
//        Role updatedRole = roleService.updateRole(1L, role);
//
//        // Assertions
//        assertNotNull(updatedRole);
//        assertEquals("admin_updated", updatedRole.getName());
//
//        // Verify interactions with the mock
//        verify(roleRepository).findById(1L);
//        verify(roleRepository).save(role);
//    }

//    @Test
//    void deleteRole() {
//        // Set up mock behavior for finding and deleting the role
//        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
//
//        // Call the method under test
//        roleService.deleteRole(1L);
//
//        // Verify interaction with the mock
//        verify(roleRepository).findById(1L);
//        verify(roleRepository).delete(role);
//    }
//
//    @Test
//    void grantRole() {
//        // This test could be for a method where roles are granted to users (example based on business logic).
//        // For simplicity, assume we have a grantRole method (you can replace it with actual logic).
//
//        // Example of mocked behavior if grantRole were implemented:
//        // when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
//        // roleService.grantRole(1L, user); // assuming 'user' is some user object
//
//        // Verify interactions if implemented.
//        // verify(roleRepository).findById(1L);
//    }

    @Test
    void getRoleByAccountId() {
        // Example: Get role by account ID logic (assumes roleRepository.findByAccountId exists)

        // Set up mock behavior
        // when(roleRepository.findByAccountId(1L)).thenReturn(Optional.of(role));

        // Call the method under test
        // Optional<Role> foundRole = roleService.getRoleByAccountId(1L);

        // Assertions
        // assertTrue(foundRole.isPresent());
        // assertEquals("admin", foundRole.get().getName());

        // Verify interaction with the mock
        // verify(roleRepository).findByAccountId(1L);
    }
}