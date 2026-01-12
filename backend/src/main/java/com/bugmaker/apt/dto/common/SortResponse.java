package com.bugmaker.apt.dto.common;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public class SortResponse {
    private final boolean sorted;
    private String direction;
    private String orderProperty;

    public SortResponse(Sort sort) {
        this.sorted = sort.isSorted();
        sort.get().forEach(propertySort -> {
            this.direction = propertySort.getDirection().toString();
            this.orderProperty = propertySort.getProperty();
        });
    }
}
