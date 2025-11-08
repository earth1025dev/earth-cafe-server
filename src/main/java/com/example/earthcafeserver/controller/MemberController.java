package com.example.earthcafeserver.controller;

import com.example.earthcafeserver.dto.member.MemberRequest;
import com.example.earthcafeserver.dto.member.MemberResponse;
import com.example.earthcafeserver.dto.member.MemberUpdateRequest;
import com.example.earthcafeserver.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "회원가입", description = "회원가입합니다.")
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody MemberRequest request) {
        MemberResponse memberResponse = memberService.insertMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
    }

    /**
     * 회원수정
     *
     * @param id
     * @param request
     * @return
     */
    @Operation(summary = "회원수정", description = "회원 정보를 수정합니다.")
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
    @Operation(summary = "회원 단건 조회", description = "회원을 단건 조회합니다.")
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
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴합니다.")
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id) {
        memberService.withdrawMember(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 회원 탈퇴 철회
     * @param id
     * @return
     */
    @Operation(summary = "회원 탈퇴 철회", description = "회원 탈퇴를 철회합니다.")
    @PostMapping("/{id}/cancel-withdrawal")
    public ResponseEntity<?> cancelWithdrawal(@PathVariable Long id) {
        memberService.cancelWithdrawal(id);
        return ResponseEntity.noContent().build();
    }
}
