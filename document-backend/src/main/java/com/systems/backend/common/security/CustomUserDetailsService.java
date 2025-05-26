package com.systems.backend.common.security;

import com.systems.backend.users.models.Account;
import com.systems.backend.users.models.Role;
import com.systems.backend.users.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    private Collection<GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(
                role -> new SimpleGrantedAuthority(role.getName().toLowerCase())
        ).collect(Collectors.toSet());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(account.getUsername(), account.getPassword(), mapRolesToAuthorities(account.getRoles()));
    }
}
