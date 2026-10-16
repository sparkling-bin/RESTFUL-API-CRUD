package com.example.RESTFUL.API.CRUD.board.dto;

// 왜 record 씀?
public record BoardCreateRequest(
        String title,
        String content) {

}
