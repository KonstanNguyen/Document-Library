package com.systems.backend.users.services;

import com.systems.backend.users.models.Role;
import com.systems.backend.users.resquests.CreateRoleRequest;
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

    void revokeRole(Long roleId, Long userId);

    List<Role> getRolesByAccountId(Long accountId);
}
