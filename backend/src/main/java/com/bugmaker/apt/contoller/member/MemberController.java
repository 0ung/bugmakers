package com.bugmaker.apt.contoller.member;


import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.service.member.MemberService;
import com.bugmaker.apt.domain.member.MemberRegisterRequest;
import com.bugmaker.apt.domain.member.NicknameCreator;
import com.bugmaker.apt.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final NicknameCreator nicknameCreator;
    private final MemberRepository memberRepository;

    @GetMapping("/about/me")
    public ResponseEntity<Member> getUser(@AuthenticationPrincipal Member member){
        return ResponseEntity.ok(member);
    }

    public MemberController(NicknameCreator nicknameCreator, MemberRepository memberRepository) {
        this.nicknameCreator = nicknameCreator;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid MemberRegisterRequest registerRequest) {

        Member member = Member.register(registerRequest, nicknameCreator);

        Member saved = memberRepository.save(member);

        saved.activate();

        return ResponseEntity.ok().build();
    }


}
