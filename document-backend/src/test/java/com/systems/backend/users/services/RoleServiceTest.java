package com.systems.backend.users.services;

import com.systems.backend.users.models.Account;
import com.systems.backend.users.models.Role;
import com.systems.backend.users.repositories.AccountRepository;
import com.systems.backend.users.repositories.RoleRepository;
import com.systems.backend.users.resquests.CreateRoleRequest;
import com.systems.backend.users.services.impl.RoleServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    private final List<Role> roles = List.of(
            new Role(1L, "admin", "Admin role", null),
            new Role(2L, "user", "User role", null),
            new Role(3L, "guest", "Guest role", null)
    );

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private final long roleId = 1L;
    private final long accountId = 1L;

    private Role role;
    private Account account;

    @BeforeEach
    void setUp() {
        role = new Role(roleId, "admin", "Admin role", new HashSet<>());
        account = new Account(accountId, "account", "password", null, null, null, null);
    }

    @Test
    void getAllRoles() {
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getAllRoles();

        assertAll(
                () -> assertNotNull(result, "The result list should not be null"),
                () -> assertFalse(result.isEmpty(), "The result list should not be empty"),
                () -> assertEquals(roles.size(), result.size(), "The result list should contain exactly one role"),
                () -> assertEquals(roleId, result.get(0).getId(), "The result list should contain exactly one role"),
                () -> assertEquals("admin", result.get(0).getName(), "The role name should match"),
                () -> assertEquals("Admin role", result.get(0).getDescription(), "The role description should match")
        );

        verify(roleRepository).findAll();
    }

    @Test
    void getRoleById_whenSuccess() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        Role foundRole = roleService.getRoleById(roleId);

        assertAll(
                () -> assertNotNull(foundRole),
                () -> assertEquals("admin", foundRole.getName()),
                () -> assertEquals("Admin role", foundRole.getDescription())
        );

        verify(roleRepository).findById(roleId);
    }

    @Test
    void getRoleById_whenNotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            roleService.getRoleById(roleId);
        });

        assertEquals("Role is not found!", thrown.getMessage(), "Exception message should match");
        verify(roleRepository).findById(roleId);
    }

    @Test
    void findByName() {
        when(roleRepository.findByName("admin")).thenReturn(Optional.of(role));

        Role foundRole = roleService.findByName("admin");

        assertAll(
                () -> assertNotNull(foundRole, "The role should not be null"),
                () -> assertEquals("admin", foundRole.getName(), "The role name should match"),
                () -> assertEquals("Admin role", foundRole.getDescription(), "The role description should match")
        );

        verify(roleRepository).findByName("admin");
    }

    @Test
    void createRole_whenSuccess() {
        CreateRoleRequest createRoleRequest = new CreateRoleRequest("admin", "Admin role");
        Role roleToCreate = Role.builder()
                .name(createRoleRequest.getName())
                .description(createRoleRequest.getDescription())
                .build();
        when(roleRepository.save(any(Role.class))).thenReturn(roleToCreate);

        Role createdRole = roleService.createRole(createRoleRequest);

        assertAll(
                () -> assertNotNull(createdRole, "The role should not be null"),
                () -> assertEquals("admin", createdRole.getName(), "The role name should match"),
                () -> assertEquals("Admin role", createdRole.getDescription(), "The role description should match")
        );

        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void createRole_whenAlreadyExists() {
        CreateRoleRequest createRoleRequest = new CreateRoleRequest("admin", "Admin role");
        when(roleRepository.findByName("admin")).thenReturn(Optional.of(role));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            roleService.createRole(createRoleRequest);
        });

        assertEquals("Role with name 'admin' already exists!", thrown.getMessage(), "Exception message should match");
        verify(roleRepository).findByName("admin");
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void updateRole_whenSuccess() {
        // Set up mock behavior for updating the role
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.saveAndFlush(any(Role.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // Update role
        role.setName("admin_updated");
        Role updatedRole = roleService.updateRole(roleId, role);

        assertAll(
                () -> assertNotNull(updatedRole, "The role should not be null"),
                () -> assertEquals("admin_updated", updatedRole.getName(), "The role name should be updated correctly"),
                () -> assertEquals("Admin role", updatedRole.getDescription(), "The role description should be updated correctly")
        );

        verify(roleRepository).findById(roleId);
        verify(roleRepository).saveAndFlush(role);
    }

    @Test
    void updateRole_NotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            roleService.updateRole(roleId, new Role(roleId, "new_admin", "Updated description", null));
        });

        assertEquals("Role is not found!", thrown.getMessage(), "Exception message should match");
        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).saveAndFlush(any(Role.class));
    }

    @Test
    void updateRole_AlreadyExists() {
        Role existingRole = new Role(2L, "admin", "Another role", null); // Different ID but same name
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.findByName("admin")).thenReturn(Optional.of(existingRole));

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            roleService.updateRole(roleId, new Role(roleId, "admin", "Updated description", null));
        });

        assertEquals("Role name 'admin' is already in use!", thrown.getMessage(), "Exception message should match");
        verify(roleRepository).findById(roleId);
        verify(roleRepository).findByName("admin");
        verify(roleRepository, never()).saveAndFlush(any(Role.class));
    }

    @Test
    void deleteRole_whenSuccess() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        roleService.deleteRole(roleId);

        doReturn(Optional.empty()).when(roleRepository).findById(roleId);

        Optional<Role> deletedRole = roleRepository.findById(roleId);
        assertTrue(deletedRole.isEmpty(), "The role should be null after deletion");

        verify(roleRepository, times(2)).findById(roleId); // Allowing 2 calls
        verify(roleRepository).delete(role);
    }

    @Test
    void deleteRole_whenNotFound() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            roleService.deleteRole(roleId);
        });

        assertEquals("Role not found!", thrown.getMessage(), "Exception message should match");
        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).delete(any(Role.class));
    }

    @Test
    void grantRole_whenSuccess() {
        account.setRoles(new HashSet<>(roles));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Return modified role

        roleService.grantRole(roleId, accountId);

        Role chekedRole = roleRepository.findById(roleId).orElse(null);

        assertAll(
                () -> assertNotNull(chekedRole.getAccounts(), "The role's accounts should not be null"),
                () -> assertTrue(chekedRole.getAccounts().contains(account), "The role should now contain the account"),
                () -> assertEquals("admin", chekedRole.getName(), "The role name should match"),
                () -> assertEquals("Admin role", chekedRole.getDescription(), "The role description should match"),
                () -> assertEquals("account", chekedRole.getAccounts().iterator().next().getUsername(), "The account should match"),
                () -> assertEquals("password", chekedRole.getAccounts().iterator().next().getPassword(), "The password should match")
        );

        verify(accountRepository).findById(accountId);
        verify(roleRepository, times(2)).findById(roleId);
        verify(roleRepository).save(role);
    }

    @Test
    void grantRole_whenRoleNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            roleService.grantRole(roleId, accountId);
        });

        assertEquals("Role is not found!", thrown.getMessage(), "Exception message should match");
        verify(accountRepository).findById(accountId);
        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void grantRole_whenAccountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty()); // ✅ Keep only this

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            roleService.grantRole(roleId, accountId);
        });

        assertEquals("Account is not found!", thrown.getMessage(), "Exception message should match");

        verify(accountRepository).findById(accountId);
        verify(roleRepository, never()).findById(anyLong()); // Role lookup should NOT happen
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void grantRole_AlreadyAssigned() {
        account.setRoles(Set.of(role)); // Account already has the role
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            roleService.grantRole(roleId, accountId);
        });

        assertEquals("Role is already granted!", thrown.getMessage(), "Exception message should match");
        verify(accountRepository).findById(accountId);
        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void getRolesByAccountId_whenSuccess() {
        account.setRoles(new HashSet<>(roles));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        List<Role> rolesByAccountId = roleService.getRolesByAccountId(accountId);

        assertAll(
                () -> assertNotNull(rolesByAccountId, "The roles should not be null"),
                () -> assertEquals(3, rolesByAccountId.size(), "The role size should be match"),
                () -> assertTrue(rolesByAccountId.containsAll(roles), "The account should contain the roles")
        );

        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getRolesByAccountId_NotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            roleService.getRolesByAccountId(accountId);
        });

        assertEquals("Account not found!", thrown.getMessage(), "Exception message should match");
        verify(accountRepository).findById(accountId);
    }

    @Test
    void getRolesByAccountId_NoRoles() {
        account.setRoles(new HashSet<>()); // Empty roles
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        List<Role> rolesByAccountId = roleService.getRolesByAccountId(accountId);

        assertAll(
                () -> assertNotNull(rolesByAccountId, "The result should not be null"),
                () -> assertTrue(rolesByAccountId.isEmpty(), "The account should have no roles assigned")
        );

        verify(accountRepository).findById(accountId);
    }
}