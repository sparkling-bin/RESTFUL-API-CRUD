package com.example.RESTFUL.API.CRUD.member;

import com.example.RESTFUL.API.CRUD.member.dto.MemberResponse;
import com.example.RESTFUL.API.CRUD.member.dto.MemberSignUpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> signup(
            @RequestBody MemberSignUpRequest request
    ) {
        MemberResponse response = memberService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
