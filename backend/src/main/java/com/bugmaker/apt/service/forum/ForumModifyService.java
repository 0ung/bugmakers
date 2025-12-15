package com.bugmaker.apt.service.forum;

import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.forum.ForumCreateRequest;
import com.bugmaker.apt.domain.forum.ForumUpdateRequest;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.repository.ForumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class ForumModifyService {
    private final ForumRepository forumRepository;

    public Forum postUp(ForumCreateRequest createRequest, Member member) {
        checkCurse(createRequest.title(), createRequest.content());

        Forum forum  = Forum.postUp(createRequest, member);

        return forumRepository.save(forum);
    }

    public Forum update(ForumUpdateRequest updateRequest) {
        checkCurse(updateRequest.title(), updateRequest.content());

        Forum forum = forumRepository.findById(updateRequest.forumId()).orElseThrow();

        forum.update(updateRequest);

        return forumRepository.save(forum);
    }

    private void checkCurse(String title, String content) {
        // FIXME 부절절한 단어 필터링하는 api 등 기능 넣을지 고민
        if (title.contains("멍청이")) {
            throw new IllegalArgumentException("부적절한 단어가 들어가있습니다.");
        }
    }

}
