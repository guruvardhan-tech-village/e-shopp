package com.business.expensetracker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "categories",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_category_name_user", columnNames = {"name", "user_id"})
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isDefault;

    @Column(name = "user_id")
    private Long userId;
}
