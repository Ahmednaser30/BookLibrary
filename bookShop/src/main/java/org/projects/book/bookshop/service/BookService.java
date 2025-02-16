package org.projects.book.bookshop.service;

import jakarta.persistence.EntityNotFoundException;
import jdk.dynalink.linker.LinkerServices;
import lombok.RequiredArgsConstructor;
import org.projects.book.bookshop.dto.*;
import org.projects.book.bookshop.entity.Book;
import org.projects.book.bookshop.entity.BookTransactionHistory;
import org.projects.book.bookshop.entity.User;
import org.projects.book.bookshop.exception.OperationNotPermittedException;
import org.projects.book.bookshop.repository.BookRepository;
import org.projects.book.bookshop.repository.BookTransactionHistoryRepository;
import org.projects.book.bookshop.repository.UserRepository;
import org.projects.book.bookshop.security.CustomUserDetails;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;

    public Long save(BookRequest request, Authentication connectedUser) {
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
         Book book = bookMapper.toBook(request);
         book.setOwner(user);
       return bookRepository.save(book).getId();
    }

    public BookResponse findById(Long bookId){
        return bookRepository.findById(bookId).map(bookMapper::toBookResponse).orElseThrow(() ->new EntityNotFoundException("Book not found"));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable,user.getId());
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
        Pageable pageable = PageRequest.of(page, size,Sort.by("creationDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);
        List<BookResponse> bookResponses = books.stream().map(bookMapper::toBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }
    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<BookTransactionHistory> books = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable,user.getId());
        List<BorrowedBookResponse> bookResponses = books.stream().map(bookMapper::toBorrowedBookResponse).toList();

        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }
    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<BookTransactionHistory> books = bookTransactionHistoryRepository.findAllReturnedBooks(pageable,user.getId());
        List<BorrowedBookResponse> bookResponses = books.stream().map(bookMapper::toBorrowedBookResponse).toList();

        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }
    public Long updateShareableStatus(Long bookId,Authentication connectedUser){
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
        if(book.getOwner().getId().equals(user.getId())){
            book.setShareable(!book.isShareable());
            return bookRepository.save(book).getId();
        }else{
            throw new OperationNotPermittedException("only owner can update book");
        }
    }
    public Long updateArchivedStatus(Long bookId,Authentication connectedUser) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
        if (book.getOwner().getId().equals(user.getId())) {
            book.setShareable(!book.isArchived());
            return bookRepository.save(book).getId();
        } else {
            throw new OperationNotPermittedException("only owner can update book");
        }
    }
        public Long borrowBook(Long bookId, Authentication connectedUser){
            Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
            if (book.isArchived() || !book.isShareable()) {
                throw new OperationNotPermittedException("this book cannot be borrowed");
            }
            CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
            User user =  userDetails.getUser();
            if (book.getOwner().getId().equals(user.getId())) {
                throw new OperationNotPermittedException("you cannot borrow your own book");
            }
            final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowed(bookId,user.getId());
    if (isAlreadyBorrowed){
        throw new OperationNotPermittedException("book is already borrowed");
    }
    BookTransactionHistory bookTransactionHistory=BookTransactionHistory.builder()
            .user(user)
            .book(book)
            .returned(false)
            .returnedApproved(false)
            .build();
            return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();

    }
    public Long returnBook(Long bookId, Authentication connectedUser){
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("this book cannot be returned");
        }
        //check if it borrows
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
        if (book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("you cannot return this book");
        }
        BookTransactionHistory bookTransactionHistory= bookTransactionHistoryRepository.findByIdAndUserId(bookId,user.getId())
                .orElseThrow(()->new OperationNotPermittedException("this book is not borrowed"));;
        if(bookTransactionHistory==null){
            throw new OperationNotPermittedException("this book is not borrowed");
        }
        bookTransactionHistory.setReturned(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }
    public Long approvedBookReturned(Long bookId, Authentication connectedUser){
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("this book cannot be approved");
        }
        //check if it borrows
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("you cannot return this book");
        }
        BookTransactionHistory bookTransactionHistory= bookTransactionHistoryRepository.findByIdAndOwnerId(bookId,user.getId())
                .orElseThrow(()->new OperationNotPermittedException("this book is not borrowed"));;
        if(bookTransactionHistory==null){
            throw new OperationNotPermittedException("this book is not borrowed");
        }
        bookTransactionHistory.setReturnedApproved(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Long bookId) {
        //check if book exists
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
        String bookCover = fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }
}
