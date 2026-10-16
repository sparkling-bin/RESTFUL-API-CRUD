package com.example.RESTFUL.API.CRUD.board;

import com.example.RESTFUL.API.CRUD.board.dto.BoardCreateRequest;
import com.example.RESTFUL.API.CRUD.board.dto.BoardResponse;
import com.example.RESTFUL.API.CRUD.board.dto.BoardUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boards")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 게시글 등록
    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(
            @RequestBody BoardCreateRequest request
    ) {
        BoardResponse response = boardService.createBoard(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 게시글 전체 조회
    @GetMapping
    public ResponseEntity<List<BoardResponse>> getBoards() {
        return ResponseEntity.ok(boardService.getBoards());
    }

    // 게시글 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoard(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(boardService.getBoard(id));
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> updateBoard(
            @PathVariable Long id,
            @RequestBody BoardUpdateRequest request
    ) {
        return ResponseEntity.ok(
                boardService.updateBoard(id, request)
        );
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long id
    ) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
