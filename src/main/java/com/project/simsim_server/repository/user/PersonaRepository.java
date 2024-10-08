package com.project.simsim_server.repository.user;

import com.project.simsim_server.domain.user.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Optional<Persona> findByPersonaCode(String code);
}
