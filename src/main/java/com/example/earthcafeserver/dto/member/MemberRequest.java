package com.example.earthcafeserver.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class MemberRequest {
    @NotBlank
    private String name;

    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = "전화번호는 010-XXXX-XXXX 형식으로 입력해야 합니다."
    )
    private String phone;

    @NotNull
    private String gender;

    @Past
    private LocalDate birthDate;

    @NotNull
    private String role;
}
