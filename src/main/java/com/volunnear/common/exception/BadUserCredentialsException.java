package com.volunnear.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BadUserCredentialsException extends  RuntimeException {
    public BadUserCredentialsException(String message) {
        super(message);
    }
}
