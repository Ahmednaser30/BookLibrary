package org.projects.book.bookshop.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

import org.projects.book.bookshop.dto.AuthenticationRequest;
import org.projects.book.bookshop.dto.AuthenticationResponse;
import org.projects.book.bookshop.dto.RegistrationRequest;
import org.projects.book.bookshop.entity.Role;
import org.projects.book.bookshop.entity.Token;
import org.projects.book.bookshop.entity.User;
import org.projects.book.bookshop.security.EmailTemplateName;
import org.projects.book.bookshop.repository.RoleRepository;
import org.projects.book.bookshop.repository.TokenRepository;
import org.projects.book.bookshop.repository.UserRepository;
import org.projects.book.bookshop.security.CustomUserDetails;
import org.projects.book.bookshop.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Value("${application.security.mailing.frontend.activation-url}")
private String activationUrl;
    public void register(RegistrationRequest request) throws MessagingException {
        Role role = roleRepository.findByName("USER").orElseThrow(()-> new IllegalStateException("Role not found"));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(role))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);
    }

    public void sendValidationEmail(User user) throws MessagingException {
String newToken = generateAndSaveActivationToken(user);
emailService.sendEmail(user.getEmail(),
        user.getFullName(),
        EmailTemplateName.ACTIVATE_ACCOUNT,
        activationUrl,
        newToken,
        "Account Activation");
    }
    private String generateAndSaveActivationToken(User user){
        String generatedToken= generateActivationCode(6) ;
        Token token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);
        return generatedToken;
    }
    public String generateActivationCode(int length){
        String characters="0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

     public AuthenticationResponse login(AuthenticationRequest request) throws MessagingException {
      //verify user credentials
       Authentication auth = authenticationManager.authenticate(new
                UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

        Map<String,Object> claims = new HashMap<>();
         CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
         claims.put("fullName", user.getFullName());
        String jwtToken = jwtService.generateToken(claims,user);
        return AuthenticationResponse.builder().
        token(jwtToken).build();
    }
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token).orElseThrow(()-> new IllegalStateException("Token not found"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw new MessagingException("code is expired  and another code sent to the email");

        }
        User user = userRepository.findById(savedToken.getUser().getId()).orElseThrow(()->new UsernameNotFoundException("user not found"));
   user.setEnabled(true);
   userRepository.save(user);
   savedToken.setValidatedAt(LocalDateTime.now());
   tokenRepository.save(savedToken);
    }
}
