package org.projects.book.bookshop.exception;


import jakarta.mail.MessagingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandling {

@ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exception) {
    return ResponseEntity.
            status(HttpStatus.UNAUTHORIZED).body(
                    ExceptionResponse.builder().
                            code(BusinessErrorCodes.ACCOUNT_LOCKED.getCode())
                            .errorDescription(BusinessErrorCodes.ACCOUNT_LOCKED.getDescription())
                            .error(exception.getMessage())
                            .build());

}

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exception) {
        return ResponseEntity.
                status(HttpStatus.UNAUTHORIZED).body(
                        ExceptionResponse.builder().
                                code(BusinessErrorCodes.ACCOUNT_DISABLED.getCode())
                                .errorDescription(BusinessErrorCodes.ACCOUNT_DISABLED.getDescription())
                                .error(exception.getMessage())
                                .build());

    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException( BadCredentialsException exception) {
        return ResponseEntity.
                status(HttpStatus.UNAUTHORIZED).body(
                        ExceptionResponse.builder().
                                code(BusinessErrorCodes.BAD_CREDENTIALS.getCode())
                                .errorDescription(BusinessErrorCodes.BAD_CREDENTIALS.getDescription())
                                .error(exception.getMessage())
                                .build());

    }
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exception) {
        return ResponseEntity.
                status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        ExceptionResponse.builder()
                                .error(exception.getMessage())
                                .build());

    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exception) {
        Set<String> errors= new HashSet<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            errors.add(error.getDefaultMessage());
        });

        return ResponseEntity.
                status(HttpStatus.BAD_REQUEST).body(
                        ExceptionResponse.builder().
                               validationErrors(errors)
                                .build());

    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {

        return ResponseEntity.
                status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        ExceptionResponse.builder()
                .errorDescription("internal server error")
                .error(exception.getMessage())
                .build());

    }
    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<ExceptionResponse> handleException(OperationNotPermittedException exception) {
        return ResponseEntity.
                status(HttpStatus.BAD_REQUEST).body(
                        ExceptionResponse.builder()
                                .error(exception.getMessage())
                                .build());
    }




    }
