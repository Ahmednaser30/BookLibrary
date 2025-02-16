package org.projects.book.bookshop.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.projects.book.bookshop.dto.AuthenticationRequest;
import org.projects.book.bookshop.dto.AuthenticationResponse;
import org.projects.book.bookshop.dto.RegistrationRequest;
import org.projects.book.bookshop.service.AuthenticationService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
@PostMapping("/register")
@ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest registrationRequest) throws MessagingException {
    authenticationService.register(registrationRequest);
        return ResponseEntity.accepted().build();
    }
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) throws MessagingException {
    return ResponseEntity.ok(authenticationService.login(request));
    }
    @GetMapping("/activate-account")
    public void confirm(@RequestParam String token) throws MessagingException {
    authenticationService.activateAccount(token);
    }
    

}
