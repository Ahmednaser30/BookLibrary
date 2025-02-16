package org.projects.book.bookshop.entity;

import jakarta.persistence.*;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;
@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
private Long id;
    @Column(unique = true)
private String name;
    @CreatedDate
    @Column(updatable = false,nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

}
