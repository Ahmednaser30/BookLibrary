package org.projects.book.bookshop.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCodes {

    NO_CODE(0,HttpStatus.NOT_IMPLEMENTED,"no code"),
    INCORRECT_CURRENT_PASSWORD(300,HttpStatus.BAD_REQUEST,"current password incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301,HttpStatus.BAD_REQUEST, "The new password does not match"),
    ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "User account is locked"),
    ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "User account is disabled"),
    BAD_CREDENTIALS(304, HttpStatus.FORBIDDEN, "Login and / or Password is incorrect");

    private final int code;
    private final HttpStatus httpStatus;
    private final String description;

    private BusinessErrorCodes(final int code, final HttpStatus httpStatus, final String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;

    }
}
