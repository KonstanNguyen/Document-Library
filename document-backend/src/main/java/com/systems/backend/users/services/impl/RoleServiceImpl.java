package com.systems.backend.users.services.impl;

import com.systems.backend.users.models.Account;
import com.systems.backend.users.models.Role;
import com.systems.backend.users.repositories.AccountRepository;
import com.systems.backend.users.repositories.RoleRepository;
import com.systems.backend.users.resquests.CreateRoleRequest;
import com.systems.backend.users.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Long roleId) {
        return roleRepository
                .findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role is not found!"));
    }

    @Override
    public List<Role> getRolesByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new ResourceNotFoundException("Account not found!")
        );
        return account.getRoles()
                .stream().toList();
    }

    @Override
    public Role findByName(String roleName) {
        return roleRepository
                .findByName(roleName)
                .orElse(null);
    }

    @Override
    public Role createRole(CreateRoleRequest createRoleRequest) {
        Optional<Role> existingRole = roleRepository.findByName(createRoleRequest.getName());
        if (existingRole.isPresent()) {
            throw new RuntimeException("Role with name '" + createRoleRequest.getName() + "' already exists!");
        }

        Role role = Role.builder()
                .name(createRoleRequest.getName())
                .description(createRoleRequest.getDescription())
                .build();
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Long roleId, Role role) {
        Role existingRole = roleRepository.findById(roleId).orElseThrow(() ->
                new ResourceNotFoundException("Role is not found!")
        );

        roleRepository.findByName(role.getName()).ifPresent(duplicateRole -> {
            if (!duplicateRole.getId().equals(roleId)) {
                throw new IllegalStateException("Role name '" + role.getName() + "' is already in use!");
            }
        });

        role.setId(existingRole.getId());

        return roleRepository.saveAndFlush(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() ->
                new ResourceNotFoundException("Role not found!")
        );
        roleRepository.delete(role);
    }

    @Override
    public void grantRole(Long roleId, Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new ResourceNotFoundException("Account is not found!")
        );

        Role role = roleRepository.findById(roleId).orElseThrow(() ->
                new ResourceNotFoundException("Role is not found!")
        );

        // Cập nhật cả 2 chiều của relationship
        account.getRoles().add(role);
        role.getAccounts().add(account);

        accountRepository.save(account);
        roleRepository.save(role);
    }

    @Override
    public void revokeRole(Long roleId, Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new ResourceNotFoundException("Account is not found!")
        );

        Role role = roleRepository.findById(roleId).orElseThrow(() ->
                new ResourceNotFoundException("Role is not found!")
        );

        account.getRoles().remove(role);
        role.getAccounts().remove(account);

        accountRepository.save(account);
        roleRepository.save(role);
    }
}
