package com.project.simsim_server.repository.user;

import com.project.simsim_server.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u WHERE u.userId =:userId AND u.userStatus = 'Y'")
    Optional<Users> findByIdAndUserStatus(@Param("userId") Long userId);

    @Query("SELECT u FROM Users u WHERE u.userStatus = 'Y' AND u.email = :email")
    Optional<Users> findByUserStatusAndEmail(@Param("email") String email);
}
