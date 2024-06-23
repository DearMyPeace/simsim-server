package com.project.simsim_server.repository.user;

import com.project.simsim_server.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Users findByName(String username);
}
