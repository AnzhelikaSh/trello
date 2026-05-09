package com.itpark.trello.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)  // ← Исправлено: было @BoardColumn
    private String username;

    @Column(nullable = false, unique = true)  // ← Исправлено: было @BoardColumn
    private String email;

    @Column(nullable = false)  // ← Исправлено: было @BoardColumn
    private String password;

    private String fullName;

    @Column(name = "created_at")  // ← Исправлено: было @BoardColumn
    private LocalDateTime createdAt = LocalDateTime.now();
}

