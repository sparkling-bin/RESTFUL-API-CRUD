package com.example.RESTFUL.API.CRUD.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}