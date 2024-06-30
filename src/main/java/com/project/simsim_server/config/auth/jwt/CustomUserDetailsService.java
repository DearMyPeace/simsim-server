package com.project.simsim_server.config.auth.jwt;

import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Users user = usersRepository.findByIdAndUserStatus(Long.parseLong(userId))
                .orElseThrow(() -> new UsernameNotFoundException("[CustomUserDetailsService loadUserByUsername()] User not found"));
        return new CustomUserDetails(user);
    }
}
