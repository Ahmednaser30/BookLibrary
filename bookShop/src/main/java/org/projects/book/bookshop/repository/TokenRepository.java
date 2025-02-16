package org.projects.book.bookshop.repository;


import org.projects.book.bookshop.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository  extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);

}
