package org.projects.book.bookshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.projects.book.bookshop.entity.Book;
import org.projects.book.bookshop.entity.BookTransactionHistory;
import org.projects.book.bookshop.entity.User;
import org.projects.book.bookshop.exception.OperationNotPermittedException;
import org.projects.book.bookshop.repository.BookRepository;
import org.projects.book.bookshop.repository.BookTransactionHistoryRepository;
import org.projects.book.bookshop.security.CustomUserDetails;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookTransactionHistoryRepository historyRepository;
    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private BookService bookService;

    private User owner;
    private User borrower;
    private Book book;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).firstName("Owen").lastName("Owner").email("owner@example.com").build();
        borrower = User.builder().id(2L).firstName("Bea").lastName("Borrower").email("borrower@example.com").build();
        book = Book.builder().id(10L).title("Clean Code").owner(owner).shareable(true).archived(false).build();
    }

    private static Authentication authenticated(User user) {
        CustomUserDetails details = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(details, null, List.of());
    }

    @Test
    void borrowBook_savesHistoryWhenBookIsAvailable() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(historyRepository.existsByBookIdAndReturnedApprovedFalse(10L)).thenReturn(false);
        when(historyRepository.save(any(BookTransactionHistory.class)))
                .thenAnswer(invocation -> {
                    BookTransactionHistory history = invocation.getArgument(0);
                    history.setId(99L);
                    return history;
                });

        Long historyId = bookService.borrowBook(10L, authenticated(borrower));

        ArgumentCaptor<BookTransactionHistory> saved = ArgumentCaptor.forClass(BookTransactionHistory.class);
        verify(historyRepository).save(saved.capture());
        assertThat(historyId).isEqualTo(99L);
        assertThat(saved.getValue().getUser()).isSameAs(borrower);
        assertThat(saved.getValue().getBook()).isSameAs(book);
        assertThat(saved.getValue().isReturned()).isFalse();
        assertThat(saved.getValue().isReturnedApproved()).isFalse();
    }

    @Test
    void borrowBook_rejectsOwnBook() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.borrowBook(10L, authenticated(owner)))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("own book");
        verify(historyRepository, never()).save(any());
    }

    @Test
    void borrowBook_rejectsBookBorrowedByAnotherUser() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(historyRepository.existsByBookIdAndReturnedApprovedFalse(10L)).thenReturn(true);

        assertThatThrownBy(() -> bookService.borrowBook(10L, authenticated(borrower)))
                .isInstanceOf(OperationNotPermittedException.class)
                .hasMessageContaining("already borrowed");
        verify(historyRepository, never()).save(any());
    }

    @Test
    void borrowBook_rejectsArchivedBook() {
        book.setArchived(true);
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.borrowBook(10L, authenticated(borrower)))
                .isInstanceOf(OperationNotPermittedException.class);
    }

    @Test
    void updateArchivedStatus_togglesArchivedAndLeavesShareableAlone() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        bookService.updateArchivedStatus(10L, authenticated(owner));

        assertThat(book.isArchived()).isTrue();
        assertThat(book.isShareable()).isTrue();
    }

    @Test
    void updateShareableStatus_rejectsNonOwner() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.updateShareableStatus(10L, authenticated(borrower)))
                .isInstanceOf(OperationNotPermittedException.class);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void uploadBookCoverPicture_rejectsNonOwner() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        MockMultipartFile cover = new MockMultipartFile("file", "cover.png", "image/png", new byte[]{1, 2, 3});

        assertThatThrownBy(() -> bookService.uploadBookCoverPicture(cover, authenticated(borrower), 10L))
                .isInstanceOf(OperationNotPermittedException.class);
        verifyNoInteractions(fileStorageService);
    }

    @Test
    void approvedBookReturned_rejectsNonOwner() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.approvedBookReturned(10L, authenticated(borrower)))
                .isInstanceOf(OperationNotPermittedException.class);
        verify(historyRepository, never()).save(any());
    }
}
