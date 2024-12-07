package com.systems.backend.service.impl;

import com.systems.backend.model.Account;
import com.systems.backend.model.Role;
import com.systems.backend.repository.AccountRepository;
import com.systems.backend.repository.RoleRepository;
import com.systems.backend.requests.CreateRoleRequest;
import com.systems.backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        return roleOptional.orElse(null);
    }

    @Override
    public Role findByName(String roleName) {
        return roleRepository.findByName(roleName).orElseThrow(() ->
                new RuntimeException("Role not found!")
        );
    }

    @Override
    public Role createRole(CreateRoleRequest createRoleRequest) {
        if (roleRepository.existsByName(createRoleRequest.getName())) {
            throw new RuntimeException("Role already exists");
        }
        Role role = Role.builder()
                .name(createRoleRequest.getName())
                .description(createRoleRequest.getDescription())
                .build();
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Long roleId, Role role) {
        Role updateRole = getRoleById(role.getId());
        if (updateRole == null) {
            throw new RuntimeException("Role is not exists");
        }

        updateRole.setName(role.getName());
        updateRole.setDescription(role.getDescription());

        return null;
    }

    @Override
    public void deleteRole(Long roleId) {
        Role role = getRoleById(roleId);
        if (role == null) {
            throw new RuntimeException("Role is not found!");
        }
        roleRepository.delete(role);
    }

    @Override
    public void grantRole(Long roleId, Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new RuntimeException("Account not found!")
        );

        Role role = getRoleById(roleId);
        if (role == null) {
            throw new RuntimeException("Role is not found!");
        }

        Set<Account> accounts = new HashSet<>(role.getAccounts());
        accounts.add(account);
        role.setAccounts(accounts);

        roleRepository.save(role);
    }
}
