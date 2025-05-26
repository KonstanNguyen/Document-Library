package com.systems.backend.users.domains.common;

import com.systems.backend.users.models.Role;
import com.systems.backend.users.resquests.CreateRoleRequest;
import com.systems.backend.users.services.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyAuthority('admin') or hasAnyAuthority('ADMIN')")
@RequestMapping(value = "/api/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Role createRole(@Valid @RequestBody CreateRoleRequest createRoleRequest) {
        return roleService.createRole(createRoleRequest);
    }

    @GetMapping("{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public Role getRole(@PathVariable Long roleId) {
        return roleService.getRoleById(roleId);
    }

    @RequestMapping(value = "{roleId}/update", method = {RequestMethod.PUT, RequestMethod.POST, RequestMethod.PATCH})
    @ResponseStatus(HttpStatus.OK)
    public Role updateRole(@PathVariable Long roleId, @RequestBody Role role) {
        return roleService.updateRole(roleId, role);
    }

    @DeleteMapping("{roleId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping("{roleId}/grant/{docUserId}")
    public void grantRole(@PathVariable Long roleId, @PathVariable Long docUserId) {
        roleService.grantRole(roleId, docUserId);
    }
}
