package org.projects.book.bookshop.configuration;

import lombok.RequiredArgsConstructor;

import org.projects.book.bookshop.entity.Role;
import org.projects.book.bookshop.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IntializeRole implements CommandLineRunner{
private final RoleRepository roleRepository;
    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName("USER").isEmpty()){
            roleRepository.save(Role.builder().name("USER").build());
        }

    }
}
