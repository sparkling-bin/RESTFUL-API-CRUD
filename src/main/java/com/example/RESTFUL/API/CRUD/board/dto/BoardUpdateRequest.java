package com.example.RESTFUL.API.CRUD.board.dto;

public record BoardUpdateRequest(
        String title,
        String content
) {
}
