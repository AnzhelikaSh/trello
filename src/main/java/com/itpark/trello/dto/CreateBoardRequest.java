package com.itpark.trello.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateBoardRequest {
    @NotBlank(message = "Название доски не может быть пустым")
    private String title;

    private String description;
}