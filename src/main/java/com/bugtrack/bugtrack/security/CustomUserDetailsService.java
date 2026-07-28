package com.bugtrack.bugtrack.security;

import com.bugtrack.bugtrack.entity.User;
import com.bugtrack.bugtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userrepo;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user= userrepo.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("user not found with email: "+ email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.getIsActive(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().getRoleName()))
        );
    }
}
