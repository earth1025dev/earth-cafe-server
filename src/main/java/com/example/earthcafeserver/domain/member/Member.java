package com.example.earthcafeserver.domain.member;

import com.example.earthcafeserver.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_phone", columnNames = "phone")
})
public class Member extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalDateTime withdrawn_at;

    protected Member() {}

    public Member(String name, String phone, Gender gender, MemberStatus status, LocalDate birthDate) {
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.status = status;
        this.birthDate = birthDate;
    }
}
