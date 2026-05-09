package com.itpark.trello.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskPositionRequest {
    @NotNull(message = "ID колонки назначения не может быть пустым")
    private Long destinationColumnId;

    @NotNull(message = "Новая позиция не может быть пустой")
    private Integer newPosition;
}