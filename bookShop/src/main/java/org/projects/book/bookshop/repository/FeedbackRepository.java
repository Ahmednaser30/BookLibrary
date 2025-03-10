package org.projects.book.bookshop.repository;

import org.projects.book.bookshop.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {

    @Query("""
SELECT feedback 
FROM Feedback feedback 
WHERE feedback.book.id = :bookId

""")
    Page<Feedback> findAllFeedbacksByBook(Long bookId, Pageable pageable);
}
