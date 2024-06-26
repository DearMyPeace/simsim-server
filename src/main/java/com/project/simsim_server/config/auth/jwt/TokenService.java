package com.project.simsim_server.config.auth.jwt;


import com.project.simsim_server.config.auth.dto.TokenDTO;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public void deleteRefreshToken(String memberKey) {
        tokenRepository.deleteById(memberKey);
    }

    @Transactional
    public void saveOrUpdate(String memberKey, String refreshToken, String accessToken) {
        TokenDTO token = tokenRepository.findByAccessToken(accessToken)
                .map(o -> o.updateRefreshToken(refreshToken))
                .orElseGet(() -> new TokenDTO(memberKey, refreshToken, accessToken));

        tokenRepository.save(token);
    }

    public TokenDTO findByAccessTokenOrThrow(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() ->
                        new JwtException("Expired or invalid JWT token"));
    }

    @Transactional
    public void updateToken(String accessToken, TokenDTO token) {
        token.updateAccessToken(accessToken);
        tokenRepository.save(token);
    }
}
