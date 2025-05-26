package com.systems.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.systems.backend.users.domains.common.RoleController;
import com.systems.backend.users.models.Role;
import com.systems.backend.users.resquests.CreateRoleRequest;
import com.systems.backend.users.services.AccountService;
import com.systems.backend.users.services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@ExtendWith(SpringExtension.class)
@WithMockUser(authorities = {"admin"})
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @MockitoBean
    private AccountService accountService;

    private final Long roleId = 1L;
    private final Long docUserId = 2L;
    private List<Role> roles;
    private Role role;
    private CreateRoleRequest createRoleRequest;

    @BeforeEach
    void setUp() {
        role = new Role(roleId, "admin", "Admin role", null);
        createRoleRequest = new CreateRoleRequest("admin", "Admin role");
        roles = List.of(
                role,
                new Role(2L, "user", "User role", null),
                new Role(3L, "guest", "Guest role", null)
        );
    }

    @Test
    void getAllRoles_ShouldReturnRoles() throws Exception {
        when(roleService.getAllRoles()).thenReturn(roles);

        mockMvc.perform(get("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))  // Check for 3 roles
                .andExpect(jsonPath("$[0].name").value("admin"))
                .andExpect(jsonPath("$[1].name").value("user"))
                .andExpect(jsonPath("$[2].name").value("guest"));

        verify(roleService).getAllRoles();
    }

    @Test
    void getAllRoles_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of());

        mockMvc.perform(get("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(roleService).getAllRoles();
    }

    @Test
    void createRole_ShouldReturnCreatedRole() throws Exception {
        when(roleService.createRole(any(CreateRoleRequest.class))).thenReturn(role);

        mockMvc.perform(post("/api/roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createRoleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("admin"));

        verify(roleService).createRole(any(CreateRoleRequest.class));
    }

    @Test
    void createRole_WhenInvalidRequestBody_ShouldReturnBadRequest() throws Exception {
        CreateRoleRequest invalidRequest = new CreateRoleRequest("", ""); // Invalid request with empty fields

        mockMvc.perform(post("/api/roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Expecting 400

        verify(roleService, never()).createRole(any(CreateRoleRequest.class));
    }

    @Test
    void createRole_WhenRoleAlreadyExists_ShouldReturnConflict() throws Exception {
        when(roleService.createRole(any(CreateRoleRequest.class)))
                .thenThrow(new IllegalStateException("Role with name 'admin' already exists!"));

        mockMvc.perform(post("/api/roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createRoleRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void getRole_ShouldReturnRole() throws Exception {
        when(roleService.getRoleById(roleId)).thenReturn(role);

        mockMvc.perform(get("/api/roles/{roleId}", roleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("admin"));

        verify(roleService).getRoleById(roleId);
    }

    @Test
    void getRole_WhenNotFound_ShouldReturn404() throws Exception {
        when(roleService.getRoleById(roleId)).thenThrow(new ResourceNotFoundException("Role not found!"));

        mockMvc.perform(get("/api/roles/{roleId}", roleId))
                .andExpect(status().isNotFound());

        verify(roleService).getRoleById(roleId);
    }

    @Test
    void updateRole_ShouldReturnUpdatedRole() throws Exception {
        when(roleService.updateRole(eq(roleId), any(Role.class))).thenReturn(role);

        mockMvc.perform(put("/api/roles/{roleId}/update", roleId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("admin"));

        verify(roleService).updateRole(eq(roleId), any(Role.class));
    }

    @Test
    void updateRole_WhenRoleNotFound_ShouldReturn404() throws Exception {
        when(roleService.updateRole(eq(roleId), any(Role.class)))
                .thenThrow(new ResourceNotFoundException("Role is not found!"));

        mockMvc.perform(put("/api/roles/{roleId}/update", roleId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(role)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRole_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/roles/{roleId}/delete", roleId).with(csrf()))
                .andExpect(status().isNoContent());

        verify(roleService).deleteRole(roleId);
    }

    @Test
    void deleteRole_WhenRoleNotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Role is not found!"))
                .when(roleService).deleteRole(roleId);

        mockMvc.perform(delete("/api/roles/{roleId}/delete", roleId).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRole_WhenRoleHasDependencies_ShouldReturn409Conflict() throws Exception {
        doThrow(new IllegalStateException("Role cannot be deleted because it has dependencies!"))
                .when(roleService).deleteRole(roleId);

        mockMvc.perform(delete("/api/roles/{roleId}/delete", roleId).with(csrf()))
                .andExpect(status().isConflict()); // Expecting 409

        verify(roleService).deleteRole(roleId);
    }

    @Test
    void grantRole_ShouldReturn202() throws Exception {
        mockMvc.perform(patch("/api/roles/{roleId}/grant/{docUserId}", roleId, docUserId).with(csrf()))
                .andExpect(status().isAccepted());

        verify(roleService).grantRole(roleId, docUserId);
    }

    @Test
    void grantRole_WhenRoleNotGrantedDueToBusinessRule_ShouldReturn409() throws Exception {
        doThrow(new IllegalStateException("User already has this role!"))
                .when(roleService).grantRole(roleId, docUserId);

        mockMvc.perform(patch("/api/roles/{roleId}/grant/{docUserId}", roleId, docUserId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict()) // Expecting 409 Conflict
                .andExpect(jsonPath("$.error").value("User already has this role!"));

        verify(roleService).grantRole(roleId, docUserId);
    }

    @Test
    void grantRole_WhenRoleNotGrantedDueToAccountMissing_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Account is not found!"))
                .when(roleService).grantRole(roleId, docUserId);

        mockMvc.perform(patch("/api/roles/{roleId}/grant/{docUserId}", roleId, docUserId).with(csrf()))
                .andExpect(status().isNotFound()); // Expecting 404

        verify(roleService).grantRole(roleId, docUserId);
    }

    @Test
    void grantRole_WhenRoleNotFound_ShouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Role not found!"))
                .when(roleService).grantRole(roleId, docUserId);

        mockMvc.perform(patch("/api/roles/{roleId}/grant/{docUserId}", roleId, docUserId).with(csrf()))
                .andExpect(status().isNotFound()); // Expecting 404
    }
}