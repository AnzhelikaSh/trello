package com.itpark.trello.controller;

import com.itpark.trello.dto.BoardDto;
import com.itpark.trello.dto.CreateBoardRequest;
import com.itpark.trello.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {


    private final BoardService boardService;


    // Создать доску
    @PostMapping
    public ResponseEntity<BoardDto> createBoard(
            @Valid @RequestBody CreateBoardRequest request,
            @RequestHeader("X-User-Id") Long userId) {  // Временно передаём userId в заголовке
        BoardDto board = boardService.createBoard(request, userId);
        return new ResponseEntity<>(board, HttpStatus.CREATED);
    }


    // Получить все доски пользователя
    @GetMapping
    public ResponseEntity<List<BoardDto>> getUserBoards(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(boardService.getUserBoards(userId));
    }


    // Получить доску по ID
    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> getBoardById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardById(id));
    }


    // Обновить доску
    @PutMapping("/{id}")
    public ResponseEntity<BoardDto> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody CreateBoardRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(boardService.updateBoard(id, request, userId));
    }


    // Удалить доску
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        boardService.deleteBoard(id, userId);
        return ResponseEntity.noContent().build();
    }


    // Добавить участника
    @PostMapping("/{boardId}/members/{userId}")
    public ResponseEntity<Void> addMember(
            @PathVariable Long boardId,
            @PathVariable Long userId) {
        boardService.addMemberToBoard(boardId, userId);
        return ResponseEntity.ok().build();
    }


    // Удалить участника
    @DeleteMapping("/{boardId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long boardId,
            @PathVariable Long userId) {
        boardService.removeMemberFromBoard(boardId, userId);
        return ResponseEntity.noContent().build();
    }
}


