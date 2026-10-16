package com.example.RESTFUL.API.CRUD.board.dto;

import com.example.RESTFUL.API.CRUD.board.Board;

import java.time.LocalDateTime;

public record BoardResponse(
        Long id,
        Long memberId,
        String nickname,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    // 엔티티를 응답 DTO로 변환
    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getMember().getId(),
                board.getMember().getNickname(),
                board.getTitle(),
                board.getContent(),
                board.getCreatedAt(),
                board.getUpdatedAt()
        );
    }
}
