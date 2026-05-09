package com.itpark.trello.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateColumnRequest {
    @NotBlank(message = "Название колонки не может быть пустым")
    private String title;
}