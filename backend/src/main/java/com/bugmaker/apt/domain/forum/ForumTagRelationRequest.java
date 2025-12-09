package com.bugmaker.apt.domain.forum;

import com.bugmaker.apt.domain.tag.Tag;

public record ForumTagRelationRequest(Forum forum, Tag tag) {
}
