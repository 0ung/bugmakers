package com.bugmaker.apt.dto.common;

import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
public class SliceResponse<T> {
    private final List<T> content;
    private final boolean first;
    private final boolean last;
    private final boolean hasNext;
    private final int page;
    private final int size;

    public SliceResponse(Slice<T> sliceContent){
        this.content = sliceContent.getContent();

        this.first = sliceContent.isFirst();
        this.last = sliceContent.isLast();
        this.hasNext = sliceContent.hasNext();
        this.page = sliceContent.getNumber() + 1;
        this.size = sliceContent.getSize();
    }


}
