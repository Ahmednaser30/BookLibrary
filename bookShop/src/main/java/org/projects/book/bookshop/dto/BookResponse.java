package org.projects.book.bookshop.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String summary;
    private String owner;
    private String isbn;
    private byte[] bookCover;
    private boolean shareable;
    private boolean archived;
    private double rate;
}
