package com.bugmaker.apt.domain.member;

import jakarta.validation.constraints.Email;

public record MemberRegisterRequest(
        @Email String email
) {
}
