package com.systems.backend.service;

import com.systems.backend.model.Role;
import com.systems.backend.requests.CreateRoleRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {
    List<Role> getAllRoles();
    Role getRoleById(Long roleId);
    Role findByName(String roleName);
    Role createRole(CreateRoleRequest createRoleRequest);
    Role updateRole(Long roleId, Role role);
    void deleteRole(Long roleId);
    void grantRole(Long roleId, Long userId);
    List<Role> getRoleByAccountId(Long accountId);
}
