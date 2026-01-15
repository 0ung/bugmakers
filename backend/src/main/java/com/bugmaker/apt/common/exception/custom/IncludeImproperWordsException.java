package com.bugmaker.apt.common.exception.custom;

import com.bugmaker.apt.common.exception.errorcode.ErrorCode;

public class IncludeImproperWordsException extends CustomException {
    public IncludeImproperWordsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
