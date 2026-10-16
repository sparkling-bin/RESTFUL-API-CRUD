package com.example.RESTFUL.API.CRUD.auth.dto;

public record TokenResponse(
        String grantType,
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn
) {
}