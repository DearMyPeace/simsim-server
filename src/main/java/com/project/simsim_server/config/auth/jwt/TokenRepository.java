package com.project.simsim_server.config.auth.jwt;

import com.project.simsim_server.config.auth.dto.TokenDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<TokenDTO, String> {

    Optional<TokenDTO> findByAccessToken(String accessToken);
}