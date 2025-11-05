package com.example.earthcafeserver.dto.member;

import com.example.earthcafeserver.domain.member.Gender;
import com.example.earthcafeserver.domain.member.MemberStatus;
import com.example.earthcafeserver.domain.member.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberResponse {

    private Long id;
    private String name;
    private String phone;
    private Gender gender;
    private Role role;
    private LocalDate birthDate;
    private MemberStatus status;
    private LocalDateTime withdrawnAt;

}
