package org.projects.book.bookshop.security;

import lombok.RequiredArgsConstructor;

import org.projects.book.bookshop.entity.User;
import org.projects.book.bookshop.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;




    @Override
    public UserDetails loadUserByUsername(String useremail) throws UsernameNotFoundException {

      User user= userRepository.findByEmail(useremail).orElseThrow(()-> new UsernameNotFoundException("user not found"));

       return new CustomUserDetails(user);

    }
}
