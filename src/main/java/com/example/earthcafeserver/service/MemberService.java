package com.example.earthcafeserver.service;

import com.example.earthcafeserver.domain.member.Gender;
import com.example.earthcafeserver.domain.member.Member;
import com.example.earthcafeserver.domain.member.MemberStatus;
import com.example.earthcafeserver.domain.member.Role;
import com.example.earthcafeserver.dto.member.MemberRequest;
import com.example.earthcafeserver.dto.member.MemberResponse;
import com.example.earthcafeserver.dto.member.MemberUpdateRequest;
import com.example.earthcafeserver.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        return toResponse(member);
    }

    public MemberResponse insertMember(MemberRequest request) {
        Member member = memberRepository.save(new Member(
                request.getName(),
                request.getPhone(),
                parseGender(request.getGender()),
                parseRole(request.getRole()),
                request.getBirthDate()
        ));

        return toResponse(member);
    }

    public MemberResponse updateMember(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        if (request.getName() != null) {
            member.setName(request.getName());
        }

        if (request.getPhone() != null) {
            // 전화번호 형식 검증 후 수정 (패턴 검증은 DTO단에서, 여기선 정책 검증)
            if (!request.getPhone().matches("^010-\\d{4}-\\d{4}$")) {
                throw new IllegalArgumentException("전화번호 형식이 올바르지 않습니다.");
            }

            // 중복 여부 확인
            if (memberRepository.existsByPhone(request.getPhone())) {
                throw new IllegalStateException("이미 사용 중인 전화번호입니다.");
            }

            member.setPhone(request.getPhone());
        }

        if (request.getGender() != null) {
            member.setGender(parseGender(request.getGender()));
        }

        if (request.getBirthDate() != null) {
            member.setBirthDate(request.getBirthDate());
        }

        return toResponse(member);
    }

    @Transactional
    public void withdrawMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new IllegalStateException("이미 탈퇴한 회원입니다.");
        }

        member.setStatus(MemberStatus.WITHDRAWN);
        member.setWithdrawnAt(LocalDateTime.now());
    }

    /*
    TODO
     */
    public void purgeWithdrawnMembers() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        memberRepository.deleteMembersWithdrawnBefore(thirtyDaysAgo);
    }

    public void cancelWithdrawal(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        if (member.getStatus() != MemberStatus.WITHDRAWN) {
            throw new IllegalStateException("탈퇴 상태의 회원만 철회할 수 있습니다.");
        }

        if (member.getWithdrawnAt().isBefore(LocalDateTime.now().minusDays(30))) {
            throw new IllegalStateException("탈퇴 철회 가능 기간을 지났습니다.");
        }

        member.setStatus(MemberStatus.ACTIVE);
        member.setWithdrawnAt(null);
    }

    /*
    TODO
     */
    public void upgradeMemberStatus() {

    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                maskPhone(member.getPhone()),
                member.getGender(),
                member.getRole(),
                member.getBirthDate(),
                member.getStatus(),
                member.getWithdrawnAt()
        );
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "-****-" + phone.substring(phone.length() - 4);
    }

    private Gender parseGender(String gender) {
        if (gender == null) {
            throw new IllegalArgumentException("gender는 필수입니다.");
        }
        return switch (gender.toUpperCase()) {
            case "M", "MALE" -> Gender.MALE;
            case "F", "FEMALE" -> Gender.FEMALE;
            default -> throw new IllegalArgumentException("지원하지 않는 성별입니다: " + gender);
        };
    }

    private Role parseRole(String role) {
        if (role == null) {
            return Role.BASIC_USER;
        }
        return switch (role.toUpperCase()) {
            case "ADMIN" -> Role.ADMIN;
            case "USER", "BASIC_USER" -> Role.BASIC_USER;
            default -> Role.BASIC_USER;
        };
    }
}
