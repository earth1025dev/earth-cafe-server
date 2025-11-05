package com.example.earthcafeserver.controller;

import com.example.earthcafeserver.domain.member.Member;
import com.example.earthcafeserver.domain.member.MemberStatus;
import com.example.earthcafeserver.dto.member.MemberRequest;
import com.example.earthcafeserver.dto.member.MemberUpdateRequest;
import com.example.earthcafeserver.repository.MemberRepository;
import com.example.earthcafeserver.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 생성 성공")
    void createMember_success() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setName("홍길동");
        request.setPhone("010-1234-5678");
        request.setGender("MALE");
        request.setRole("BASIC_USER");
        request.setBirthDate(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.phone").exists());
    }

    @Test
    @DisplayName("회원 생성 실패")
    void createMember_fail() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setName("홍길동");
        request.setPhone("010-1234-56789");
        request.setGender("MALE");
        request.setRole("BASIC_USER");
        request.setBirthDate(LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("전화번호는 010-XXXX-XXXX 형식으로 입력해야 합니다."));
    }

    @Test
    @DisplayName("회원 탈퇴 요청")
    void withdrawMember() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setName("홍길동");
        request.setPhone("010-1234-5679");
        request.setGender("MALE");
        request.setRole("BASIC_USER");
        request.setBirthDate(LocalDate.of(1990, 1, 1));

        String response = mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(response).get("id").asLong();

        mockMvc.perform(post("/api/members/{id}/withdraw", id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원 수정 요청")
    void updateMember() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setName("홍길동");
        request.setPhone("010-1234-5678");
        request.setGender("MALE");
        request.setRole("BASIC_USER");
        request.setBirthDate(LocalDate.of(1990, 1, 1));

        String response = mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(response).get("id").asLong();

        MemberUpdateRequest update = new MemberUpdateRequest();
        update.setName("성춘향");
        update.setGender("FEMALE");

        mockMvc.perform(post("/api/members/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("성춘향"))
                .andExpect(jsonPath("$.gender").value("FEMALE"));
    }

    @Test
    @DisplayName("탈퇴 후 30일 이내면 탈퇴 철회 성공")
    void cancelWithdrawal_success() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setName("홍길동");
        request.setPhone("010-1234-5678");
        request.setGender("MALE");
        request.setRole("BASIC_USER");
        request.setBirthDate(LocalDate.of(1990, 1, 1));

        // 1) 회원 생성
        String response = mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(response).get("id").asLong();

        // 2) 탈퇴
        mockMvc.perform(post("/api/members/{id}/withdraw", id))
                .andExpect(status().isOk());

        // 3) 탈퇴 철회
        mockMvc.perform(post("/api/members/{id}/cancel-withdrawal", id))
                .andExpect(status().isOk());

        // 4) 다시 조회하여 활성화 상태값 확인
        mockMvc.perform(get("/api/members/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("탈퇴 후 30일 이후면 탈퇴 철회 실패")
    void cancelWithdrawal_fail() throws Exception {
        MemberRequest request = new MemberRequest();
        request.setName("홍길동");
        request.setPhone("010-1234-5678");
        request.setGender("MALE");
        request.setRole("BASIC_USER");
        request.setBirthDate(LocalDate.of(1990, 1, 1));

        // 1) 회원 생성
        String response = mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(response).get("id").asLong();

        // 2) 회원 조회 후 철회 일자 조정
        Member member = memberRepository.findById(id).get();
        member.setStatus(MemberStatus.WITHDRAWN);
        member.setWithdrawnAt(LocalDateTime.now().minusDays(40));
        memberRepository.save(member);

        // 3) 탈퇴 철회
        mockMvc.perform(post("/api/members/{id}/cancel-withdrawal", id))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof IllegalStateException))
                .andExpect(result -> assertEquals(
                        "탈퇴 철회 가능 기간을 지났습니다.",
                        result.getResolvedException().getMessage()
                ));

    }
}