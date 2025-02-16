package org.projects.book.bookshop.service;

import org.projects.book.bookshop.dto.BookRequest;
import org.projects.book.bookshop.dto.BookResponse;
import org.projects.book.bookshop.dto.BorrowedBookResponse;
import org.projects.book.bookshop.entity.Book;
import org.projects.book.bookshop.entity.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public Book toBook(BookRequest bookRequest) {
        return Book.builder()
                .title(bookRequest.title())
                .isbn(bookRequest.isbn())
                .author(bookRequest.author())
                .summary(bookRequest.summary())
                .archived(false)
                .shareable(bookRequest.shareable())
                .build();
    }
    public BookResponse toBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .summary(book.getSummary())
                .rate(book.getRating())
                .owner(book.getOwner().getFullName())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .bookCover(FileUtils.readFileFromLocation(book.getBookCover()))
                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return  BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .isbn(history.getBook().getIsbn())
                .rate(history.getBook().getRating())
                .returned(history.isReturned())
                .returnedApproved(history.isReturnedApproved())
                .build();
    }

}
