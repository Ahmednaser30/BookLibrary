package org.projects.book.bookshop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.projects.book.bookshop.entity.Book;
import org.projects.book.bookshop.entity.BookTransactionHistory;
import org.projects.book.bookshop.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

// JPA auditing is not part of the @DataJpaTest slice, so audit columns are filled in by hand
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookTransactionHistoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookTransactionHistoryRepository repository;

    private User owner;
    private User borrower;
    private Book book;

    @BeforeEach
    void setUp() {
        owner = entityManager.persist(user("owner@example.com"));
        borrower = entityManager.persist(user("borrower@example.com"));
        book = entityManager.persist(book(owner, "Clean Code"));
    }

    @Test
    void bookIsBorrowedUntilReturnIsApproved() {
        BookTransactionHistory history = entityManager.persist(history(borrower, book, false, false));
        assertThat(repository.existsByBookIdAndReturnedApprovedFalse(book.getId())).isTrue();

        history.setReturned(true);
        entityManager.flush();
        assertThat(repository.existsByBookIdAndReturnedApprovedFalse(book.getId())).isTrue();

        history.setReturnedApproved(true);
        entityManager.flush();
        assertThat(repository.existsByBookIdAndReturnedApprovedFalse(book.getId())).isFalse();
    }

    @Test
    void borrowOfAnotherBookDoesNotBlockThisOne() {
        Book other = entityManager.persist(book(owner, "Refactoring"));
        entityManager.persist(history(borrower, other, false, false));

        assertThat(repository.existsByBookIdAndReturnedApprovedFalse(book.getId())).isFalse();
    }

    @Test
    void findByIdAndUserId_findsOnlyTheBorrowersOpenBorrow() {
        entityManager.persist(history(borrower, book, false, false));

        assertThat(repository.findByIdAndUserId(book.getId(), borrower.getId())).isPresent();
        assertThat(repository.findByIdAndUserId(book.getId(), owner.getId())).isEmpty();
    }

    @Test
    void findByIdAndOwnerId_findsReturnedBorrowAwaitingApproval() {
        entityManager.persist(history(borrower, book, true, false));

        assertThat(repository.findByIdAndOwnerId(book.getId(), owner.getId())).isPresent();
        assertThat(repository.findByIdAndOwnerId(book.getId(), borrower.getId())).isEmpty();
    }

    @Test
    void findAllBorrowedBooks_andFindAllReturnedBooks_pageByUserAndOwner() {
        entityManager.persist(history(borrower, book, false, false));
        PageRequest page = PageRequest.of(0, 10);

        assertThat(repository.findAllBorrowedBooks(page, borrower.getId()).getTotalElements()).isEqualTo(1);
        assertThat(repository.findAllBorrowedBooks(page, owner.getId()).getTotalElements()).isZero();
        assertThat(repository.findAllReturnedBooks(page, owner.getId()).getTotalElements()).isEqualTo(1);
    }

    private static User user(String email) {
        return User.builder()
                .firstName("Test")
                .lastName("User")
                .email(email)
                .password("hashed")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static Book book(User owner, String title) {
        return Book.builder()
                .title(title)
                .author("Author")
                .isbn("isbn-" + title)
                .summary("summary")
                .shareable(true)
                .archived(false)
                .owner(owner)
                .creationDate(LocalDate.now())
                .creatorId(owner.getId())
                .build();
    }

    private static BookTransactionHistory history(User user, Book book, boolean returned, boolean approved) {
        return BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(returned)
                .returnedApproved(approved)
                .creationDate(LocalDate.now())
                .creatorId(user.getId())
                .build();
    }
}
