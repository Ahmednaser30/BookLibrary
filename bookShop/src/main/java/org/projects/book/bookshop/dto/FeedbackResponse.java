package org.projects.book.bookshop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class FeedbackResponse {
    private Double rate;
    private String comment;
    private boolean ownFeedback;
}
