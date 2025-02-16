package org.projects.book.bookshop.service;

import org.projects.book.bookshop.dto.FeedbackRequest;
import org.projects.book.bookshop.dto.FeedbackResponse;
import org.projects.book.bookshop.entity.Book;
import org.projects.book.bookshop.entity.Feedback;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

    public Feedback toFeedBack(FeedbackRequest request){
        return Feedback.builder()
                .feedback(request.getComment())
                .note(request.getRate())
                .book(Book.builder()
                        .id(request.getBookId())
                        .shareable(false)
                        .archived(false)
                        .build())
                .build();
    }
    public FeedbackResponse toFeedBackResponse(Feedback feedback,Long userId){
        return FeedbackResponse.builder()
                .rate(feedback.getNote())
                .comment(feedback.getFeedback())
                .ownFeedback(Objects.equals(feedback.getCreatorId(),userId))
                .build();
    }
}
