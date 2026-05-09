package com.itpark.trello.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Название задачи не может быть пустым")
    private String title;

    private String description;

    private Long assigneeId;  // ← Добавь это поле (ID исполнителя)
}
