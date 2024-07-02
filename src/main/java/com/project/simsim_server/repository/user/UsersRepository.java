package com.project.simsim_server.repository.user;

import com.project.simsim_server.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    @Query("SELECT u FROM Users u WHERE u.email =:userEmail AND u.userStatus = 'Y'")
    Optional<Users> findByEmailAndUserStatus(@Param("userEmail") String userEmail);

    @Query("SELECT u FROM Users u WHERE u.userId =:userId AND u.userStatus = 'Y'")
    Optional<Users> findByIdAndUserStatus(@Param("userId") Long userId);

    Optional<Users> findByEmail(String userEmail);

    Optional<String> findPersonaByUserStatus(String userStatus);

    @Query("SELECT u FROM Users u WHERE u.userStatus = 'Y'")
    List<Users> findAllAndUserStatus();
}
