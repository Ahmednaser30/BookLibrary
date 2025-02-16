package org.projects.book.bookshop.base;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    @CreatedDate
    @Column(updatable = false,nullable = false)
    private LocalDate creationDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDate modificationDate;
    @CreatedBy
    @Column(updatable = false,nullable = false)
    private Long creatorId;
    @LastModifiedBy
    @Column(insertable = false)
    private Long modifierId;
}
