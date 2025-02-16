package org.projects.book.bookshop.dto;

import org.projects.book.bookshop.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book>  withOwnerId(Long id) {
        return ((root, query, criteriaBuilder) ->
                  criteriaBuilder.equal(root.get("owner").get("id"), id));
    }
}
