package com.example.RESTFUL.API.CRUD.member;

import com.example.RESTFUL.API.CRUD.member.dto.MemberResponse;
import com.example.RESTFUL.API.CRUD.member.dto.MemberSignUpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public MemberResponse signup(MemberSignUpRequest request) {
        if (request.email() == null || request.email().isBlank()
                || request.password() == null || request.password().isBlank()
                || request.nickname() == null || request.nickname().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "모든 항목을 입력해 주세요."
            );
        }

        if (request.password().length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상이어야 합니다."
            );
        }

        if (memberRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        Member member = new Member(
                request.email(),
                encodedPassword,
                request.nickname()
        );

        return MemberResponse.from(memberRepository.save(member));
    }
}
