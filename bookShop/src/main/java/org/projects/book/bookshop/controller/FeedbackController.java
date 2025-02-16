package org.projects.book.bookshop.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.projects.book.bookshop.dto.FeedbackRequest;
import org.projects.book.bookshop.dto.FeedbackResponse;
import org.projects.book.bookshop.dto.PageResponse;
import org.projects.book.bookshop.entity.Feedback;
import org.projects.book.bookshop.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("feedbacks")
@Tag(name = "feedbacks")
public class FeedbackController {
    private final FeedbackService feedbackService;
    @PostMapping
    public ResponseEntity<Long> saveFeedback(@Valid @RequestBody FeedbackRequest request
    , Authentication connectedUser){
        return ResponseEntity.ok(feedbackService.save(request,connectedUser));

    }
    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBookId(
            @PathVariable("book-id") Long bookId,
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser){
        return ResponseEntity.ok(feedbackService.findAllFeedbacksByBook(bookId,page,size,connectedUser));
    }

}
