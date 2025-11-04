package com.example.earthcafeserver.service;

import com.example.earthcafeserver.domain.member.Gender;
import com.example.earthcafeserver.domain.member.Member;
import com.example.earthcafeserver.domain.member.MemberStatus;
import com.example.earthcafeserver.dto.MemberRequest;
import com.example.earthcafeserver.dto.MemberResponse;
import com.example.earthcafeserver.dto.MemberUpdateRequest;
import com.example.earthcafeserver.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    private MemberRequest createRequest() {
        MemberRequest request = new MemberRequest();
        request.setName("홍길동");
        request.setPhone("010-1234-5678");
        request.setGender("MALE");
        request.setRole("BASIC_USER");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        return request;
    }

    @Test
    void insertMember() {
        // given
        MemberRequest request = createRequest();

        // when
        MemberResponse member = memberService.insertMember(request);

        // then
        assertNotNull(member.getId());
        assertEquals("홍길동", member.getName());
        assertEquals("010-****-5678", member.getPhone());
        assertEquals(MemberStatus.ACTIVE, member.getStatus());
    }

    @Test
    void updateMember() {
        // given
        MemberResponse saved = memberService.insertMember(createRequest());
        MemberUpdateRequest update = new MemberUpdateRequest();
        update.setName("성춘향");
        update.setGender("FEMALE");

        // when
        MemberResponse memberResponse = memberService.updateMember(saved.getId(), update);

        // then
        assertEquals("성춘향", memberResponse.getName());
        assertEquals(Gender.FEMALE, memberResponse.getGender());
    }

    @Test
    void withdrawMember() {
        // given
        MemberResponse saved = memberService.insertMember(createRequest());

        // when
        memberService.withdrawMember(saved.getId());

        // then
        MemberResponse member = memberService.getMember(saved.getId());
        assertEquals(MemberStatus.WITHDRAWN, member.getStatus());
        assertNotNull(member.getWithdrawnAt());
    }

    @Test
    void cancelWithdrawal_under30days() {
        // given
        MemberResponse saved = memberService.insertMember(createRequest());
        memberService.withdrawMember(saved.getId());

        // when
        memberService.cancelWithdrawal(saved.getId());

        //then
        MemberResponse memberResponse = memberService.getMember(saved.getId());
        assertEquals(MemberStatus.ACTIVE, memberResponse.getStatus());
        assertNull(memberResponse.getWithdrawnAt());
    }

    @Test
    void cancelWithdrawal_after30days() {
        // given
        MemberResponse saved = memberService.insertMember(createRequest());
        Member member = memberRepository.findById(saved.getId()).orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));
        member.setStatus(MemberStatus.WITHDRAWN);
        member.setWithdrawnAt(LocalDateTime.now().minusDays(40));
        memberRepository.save(member);

        // then
        assertThrows(IllegalStateException.class, () ->
                memberService.cancelWithdrawal(member.getId())
        );
    }
}