package com.ikub.reservationapp.common.exception;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PasswordNotValidException extends RuntimeException {

    private List<String> messages;

    public PasswordNotValidException(List<String> messages) {
        super(StringUtils.join(messages, ", "));
        this.messages = messages;
    }

    public PasswordNotValidException() {
    }
}