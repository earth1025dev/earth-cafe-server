package com.example.earthcafeserver.controller;

import com.example.earthcafeserver.dto.MemberRequest;
import com.example.earthcafeserver.dto.MemberResponse;
import com.example.earthcafeserver.dto.MemberUpdateRequest;
import com.example.earthcafeserver.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     *
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody MemberRequest request) {
        MemberResponse memberResponse = memberService.insertMember(request);
        return ResponseEntity.ok(memberResponse);
    }

    /**
     * 회원수정
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @Valid @RequestBody MemberUpdateRequest request) {
        MemberResponse memberResponse = memberService.updateMember(id, request);
        return ResponseEntity.ok(memberResponse);
    }

    /**
     * 회원 단건 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMember(@PathVariable Long id) {
        MemberResponse member = memberService.getMember(id);
        return ResponseEntity.ok(member);
    }

    /**
     * 회원 탈퇴
     * @param id
     * @return
     */
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id) {
        memberService.withdrawMember(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 탈퇴 철회
     * @param id
     * @return
     */
    @PostMapping("/{id}/cancel-withdrawal")
    public ResponseEntity<?> cancelWithdrawal(@PathVariable Long id) {
        memberService.cancelWithdrawal(id);
        return ResponseEntity.ok().build();
    }
}
