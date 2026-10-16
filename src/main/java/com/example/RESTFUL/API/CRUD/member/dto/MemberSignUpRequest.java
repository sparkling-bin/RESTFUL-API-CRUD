package com.example.RESTFUL.API.CRUD.member.dto;

public record MemberSignUpRequest (
        String email,
        String password,
        String nickname
) {
}
