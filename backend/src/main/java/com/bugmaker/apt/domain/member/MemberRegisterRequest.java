package com.bugmaker.apt.domain.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record MemberRegisterRequest(
        @Email String email,
        @Size(min = 8, max = 30) String password,
        @Size(min = 2, max = 20) String name,
        String phone
) {
}
