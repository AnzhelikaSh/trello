package com.itpark.trello.controller;

import com.itpark.trello.dto.BoardDto;
import com.itpark.trello.dto.CreateBoardRequest;
import com.itpark.trello.model.User;
import com.itpark.trello.service.BoardService;
import com.itpark.trello.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;

    // Создать доску
    @PostMapping
    public ResponseEntity<BoardDto> createBoard(
            @Valid @RequestBody CreateBoardRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsernameEntity(userDetails.getUsername());
        BoardDto board = boardService.createBoard(request, currentUser.getId());
        return new ResponseEntity<>(board, HttpStatus.CREATED);
    }

    // Получить все доски текущего пользователя
    @GetMapping
    public ResponseEntity<List<BoardDto>> getUserBoards(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsernameEntity(userDetails.getUsername());
        return ResponseEntity.ok(boardService.getUserBoards(currentUser.getId()));
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
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsernameEntity(userDetails.getUsername());
        return ResponseEntity.ok(boardService.updateBoard(id, request, currentUser.getId()));
    }

    // Удалить доску
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsernameEntity(userDetails.getUsername());
        boardService.deleteBoard(id, currentUser.getId());
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
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getUserByUsernameEntity(userDetails.getUsername());
        boardService.removeMemberFromBoard(boardId, userId);
        return ResponseEntity.noContent().build();
    }
}



