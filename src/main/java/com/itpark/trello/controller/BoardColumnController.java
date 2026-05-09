package com.itpark.trello.controller;

import com.itpark.trello.dto.BoardColumnDto;
import com.itpark.trello.dto.CreateColumnRequest;
import com.itpark.trello.service.BoardColumnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/boards/{boardId}/columns")
@RequiredArgsConstructor
public class BoardColumnController {


    private final BoardColumnService columnService;


    // Создать колонку в доске
    @PostMapping
    public ResponseEntity<BoardColumnDto> createColumn(
            @PathVariable Long boardId,
            @Valid @RequestBody CreateColumnRequest request) {
        BoardColumnDto column = columnService.createColumn(boardId, request);
        return new ResponseEntity<>(column, HttpStatus.CREATED);
    }


    // Получить все колонки доски
    @GetMapping
    public ResponseEntity<List<BoardColumnDto>> getBoardColumns(@PathVariable Long boardId) {
        return ResponseEntity.ok(columnService.getBoardColumns(boardId));
    }


    // Обновить колонку
    @PutMapping("/{columnId}")
    public ResponseEntity<BoardColumnDto> updateColumn(
            @PathVariable Long boardId,
            @PathVariable Long columnId,
            @Valid @RequestBody CreateColumnRequest request) {
        return ResponseEntity.ok(columnService.updateColumn(columnId, request));
    }


    // Удалить колонку
    @DeleteMapping("/{columnId}")
    public ResponseEntity<Void> deleteColumn(
            @PathVariable Long boardId,
            @PathVariable Long columnId) {
        columnService.deleteColumn(columnId);
        return ResponseEntity.noContent().build();
    }


    // Изменить порядок колонок
    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorderColumns(
            @PathVariable Long boardId,
            @RequestBody List<Long> columnIdsInOrder) {
        columnService.reorderColumns(boardId, columnIdsInOrder);
        return ResponseEntity.ok().build();
    }
}

