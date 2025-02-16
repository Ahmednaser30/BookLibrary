package org.projects.book.bookshop.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.projects.book.bookshop.base.BaseEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BaseEntity {


    private String title;
    private String author;
    private String isbn;
    private String summary;
    private String bookCover;
    private boolean shareable;
    private boolean archived;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    @JsonManagedReference
    private List<Feedback> feedbacks;

    @Transient
    public double getRating() {
        if(feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
            double rate = this.feedbacks.stream().mapToDouble(Feedback::getNote).average().orElse(0.0);
        return Math.round(rate * 10.0) / 10.0;

    }
}
