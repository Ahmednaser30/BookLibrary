package org.projects.book.bookshop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BorrowedBookResponse {
    private Long id;
    private String authorName;
    private String title;
    private String isbn;
    private boolean returned;
    private boolean returnedApproved;
    private double rate;
}
