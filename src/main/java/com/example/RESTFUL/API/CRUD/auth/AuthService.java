package com.example.RESTFUL.API.CRUD.auth;

import com.example.RESTFUL.API.CRUD.auth.dto.LoginRequest;
import com.example.RESTFUL.API.CRUD.auth.dto.RefreshRequest;
import com.example.RESTFUL.API.CRUD.auth.dto.TokenResponse;
import com.example.RESTFUL.API.CRUD.global.JwtTokenProvider;
import com.example.RESTFUL.API.CRUD.member.Member;
import com.example.RESTFUL.API.CRUD.member.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            MemberRepository memberRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(this::unauthorized);

        if (member.getPasswordHash() == null
                || !passwordEncoder.matches(
                request.password(), member.getPasswordHash())) {
            throw unauthorized();
        }

        String accessToken =
                jwtTokenProvider.generateAccessToken(member.getEmail());

        String refreshToken =
                jwtTokenProvider.generateRefreshToken(member.getEmail());

        saveRefreshToken(member, refreshToken);

        return tokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        String oldToken = request.refreshToken();

        if (!jwtTokenProvider.validateToken(oldToken, "refresh")) {
            throw unauthorized();
        }

        String email = jwtTokenProvider.getEmail(oldToken);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(this::unauthorized);

        RefreshToken savedToken =
                refreshTokenRepository.findByMember_Id(member.getId())
                        .orElseThrow(this::unauthorized);

        if (!savedToken.getToken().equals(oldToken)
                || !savedToken.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw unauthorized();
        }

        String newAccessToken =
                jwtTokenProvider.generateAccessToken(member.getEmail());

        String newRefreshToken =
                jwtTokenProvider.generateRefreshToken(member.getEmail());

        savedToken.update(
                newRefreshToken,
                LocalDateTime.now().plusNanos(
                        jwtTokenProvider.getRefreshTokenExpiration() * 1_000_000L
                )
        );

        return tokenResponse(newAccessToken, newRefreshToken);
    }

    private void saveRefreshToken(Member member, String token) {
        LocalDateTime expiresAt = LocalDateTime.now().plusNanos(
                jwtTokenProvider.getRefreshTokenExpiration() * 1_000_000L
        );

        RefreshToken saved = refreshTokenRepository
                .findByMember_Id(member.getId())
                .orElse(null);

        if (saved == null) {
            refreshTokenRepository.save(
                    new RefreshToken(member, token, expiresAt)
            );
        } else {
            saved.update(token, expiresAt);
        }
    }

    private TokenResponse tokenResponse(
            String accessToken,
            String refreshToken
    ) {
        return new TokenResponse(
                "Bearer",
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenExpiration() / 1000
        );
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "이메일, 비밀번호 또는 토큰을 확인해 주세요."
        );
    }
}
