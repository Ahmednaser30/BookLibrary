package org.projects.book.bookshop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.projects.book.bookshop.base.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BookTransactionHistory extends BaseEntity {

    private boolean returned;
    private boolean returnedApproved;

     @ManyToOne
     @JoinColumn(name = "user_id")
     private User user;

     @ManyToOne
     private Book book;


}
