package org.projects.book.bookshop.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.projects.book.bookshop.dto.*;
import org.projects.book.bookshop.entity.Book;
import org.projects.book.bookshop.entity.Feedback;
import org.projects.book.bookshop.entity.User;
import org.projects.book.bookshop.exception.OperationNotPermittedException;
import org.projects.book.bookshop.repository.BookRepository;
import org.projects.book.bookshop.repository.FeedbackRepository;
import org.projects.book.bookshop.security.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Long save(FeedbackRequest request, Authentication connectedUser) {
        Book book=bookRepository.findById(request.getBookId()).orElseThrow(()-> new EntityNotFoundException("book not found"));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("you cannot give feedback to this book");
        }

        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
        if (book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("you cannot give feedback to your own book");
        }
Feedback feedback=feedbackMapper.toFeedBack(request);
        return feedbackRepository.save(feedback).getId();
    }

    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Long bookId, int page, int size, Authentication connectedUser) {
        CustomUserDetails userDetails = (CustomUserDetails) connectedUser.getPrincipal();
        User user =  userDetails.getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Feedback> feedbacks = feedbackRepository.findAllFeedbacksByBook(bookId,pageable);
        List<FeedbackResponse> responses = feedbacks.stream().map(f-> feedbackMapper.toFeedBackResponse(f, user.getId())).toList();
        return new PageResponse<>(
                responses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
