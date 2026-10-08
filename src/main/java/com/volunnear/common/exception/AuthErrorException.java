package com.volunnear.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthErrorException extends RuntimeException {
    public AuthErrorException(String message) {
        super(message);
    }
}
