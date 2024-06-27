package com.project.simsim_server.repository.setting;

import com.project.simsim_server.domain.setting.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Long> {
}
