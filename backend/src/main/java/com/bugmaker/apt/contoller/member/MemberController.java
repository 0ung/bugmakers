package com.bugmaker.apt.contoller.member;


import com.bugmaker.apt.common.util.JwtUtil;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.member.MemberRegisterRequest;
import com.bugmaker.apt.domain.member.NicknameCreator;
import com.bugmaker.apt.repository.member.MemberRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final NicknameCreator nicknameCreator;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public MemberController(NicknameCreator nicknameCreator, MemberRepository memberRepository,JwtUtil jwtUtil) {
        this.nicknameCreator = nicknameCreator;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/about/me")
    public ResponseEntity<Member> getUser(@AuthenticationPrincipal Member member){
        return ResponseEntity.ok(member);
    }

    @GetMapping("/accessToken")
    public ResponseEntity<?> testing(){
        return ResponseEntity.ok(jwtUtil.generateAccessToken(1L,"gupo941020@naver.com"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid MemberRegisterRequest registerRequest) {

        Member member = Member.register(registerRequest, nicknameCreator);

        Member saved = memberRepository.save(member);

        saved.activate();

        return ResponseEntity.ok().build();
    }


}
