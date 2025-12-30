package com.bugmaker.apt.contoller.member;

import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.member.MemberRegisterRequest;
import com.bugmaker.apt.domain.member.NicknameCreator;
import com.bugmaker.apt.repository.MemberRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 테스트용 컨트롤러(수정 예정)
 */
@RestController
@RequestMapping("/member")
public class MemberController {

    private final NicknameCreator nicknameCreator;
    private final MemberRepository memberRepository;

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
