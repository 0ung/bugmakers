package com.bugmaker.apt.service.forum;

import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.forum.ForumCreateRequest;
import com.bugmaker.apt.repository.ForumRepository;
import com.bugmaker.apt.repository.TagRepository;
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
    private final TagRepository tagRepository;

}
