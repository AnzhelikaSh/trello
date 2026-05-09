package com.itpark.trello.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Integer position;
    private UserDto assignee;  // ← Добавь это поле
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}