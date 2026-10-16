package com.example.RESTFUL.API.CRUD.board;

import com.example.RESTFUL.API.CRUD.board.dto.BoardCreateRequest;
import com.example.RESTFUL.API.CRUD.board.dto.BoardResponse;
import com.example.RESTFUL.API.CRUD.board.dto.BoardUpdateRequest;
import com.example.RESTFUL.API.CRUD.member.Member;
import com.example.RESTFUL.API.CRUD.member.MemberRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public BoardService(
            BoardRepository boardRepository,
            MemberRepository memberRepository
    ) {
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public BoardResponse createBoard(BoardCreateRequest request) {
        Member member = getCurrentMember();

        Board board = new Board(
                member,
                request.title(),
                request.content()
        );

        return BoardResponse.from(boardRepository.save(board));
    }

    public List<BoardResponse> getBoards() {
        return boardRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
                .stream()
                .map(BoardResponse::from)
                .toList();
    }

    public BoardResponse getBoard(Long id) {
        Board board = findBoardById(id);
        return BoardResponse.from(board);
    }

    @Transactional
    public BoardResponse updateBoard(
            Long id,
            BoardUpdateRequest request
    ) {
        Board board = findBoardById(id);
        verifyOwner(board);

        board.update(request.title(), request.content());

        return BoardResponse.from(board);
    }

    @Transactional
    public void deleteBoard(Long id) {
        Board board = findBoardById(id);
        verifyOwner(board);

        boardRepository.delete(board);
    }

    private Board findBoardById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + id));
    }

    private Member getCurrentMember() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."
            );
        }

        return memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "회원 정보를 찾을 수 없습니다."
                ));
    }

    private void verifyOwner(Board board) {
        Member currentMember = getCurrentMember();

        if (!board.getMember().getId().equals(currentMember.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "본인이 작성한 게시글만 수정하거나 삭제할 수 있습니다."
            );
        }
    }
}
