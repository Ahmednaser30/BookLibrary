package org.projects.book.bookshop.repository;

import org.projects.book.bookshop.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @Query("select book from Book book where book.archived=false and book.shareable=true  and book.owner.id != :userId")
    Page<Book> findAllDisplayableBooks(Pageable pageable,@Param("userId") Long userId);

}
