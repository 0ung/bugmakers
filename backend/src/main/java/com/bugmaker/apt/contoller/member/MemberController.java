package com.bugmaker.apt.contoller.member;


import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/about/me")
    public ResponseEntity<Member> getUser(@AuthenticationPrincipal Member member){
        return ResponseEntity.ok(member);
    }

}
