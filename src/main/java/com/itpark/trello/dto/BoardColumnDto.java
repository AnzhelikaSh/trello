package com.itpark.trello.dto;

import lombok.Data;
import java.util.List;

@Data
public class BoardColumnDto {
    private Long id;
    private String title;
    private Integer position;
    private List<TaskDto> tasks;
}