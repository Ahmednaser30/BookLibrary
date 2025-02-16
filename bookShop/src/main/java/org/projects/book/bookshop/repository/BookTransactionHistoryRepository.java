package org.projects.book.bookshop.repository;

import org.projects.book.bookshop.entity.BookTransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {
    @Query("select  history from BookTransactionHistory history where history.user.id =:id")
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable,@Param("id") Long id);
    @Query("select  history from BookTransactionHistory history where history.book.owner.id =:id")
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable,@Param("id") Long id);
    @Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.id = :userId
            AND bookTransactionHistory.book.id = :bookId
            AND bookTransactionHistory.returnedApproved = false
            """)
    boolean isAlreadyBorrowed(@Param("bookId") Long bookId,@Param("userId") Long userId);

    @Query("""
          SELECT history
          FROM BookTransactionHistory  history
          WHERE history.book.id = :bookId
          AND history.user.id = :id
          AND history.returnedApproved = false
          AND history.returned = false
          """)
    Optional<BookTransactionHistory> findByIdAndUserId(@Param("bookId")Long bookId,@Param("id") Long id);
    @Query("""
          SELECT history
          FROM BookTransactionHistory  history
          WHERE history.book.id = :bookId
          AND history.book.owner.id = :id
          AND history.returnedApproved = false
          AND history.returned = true
          """)
    Optional<BookTransactionHistory> findByIdAndOwnerId(@Param("bookId")Long bookId,@Param("id") Long id);
}
