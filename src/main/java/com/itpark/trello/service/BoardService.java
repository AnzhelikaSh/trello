package com.itpark.trello.service;

import com.itpark.trello.dto.BoardDto;
import com.itpark.trello.dto.CreateBoardRequest;
import com.itpark.trello.exception.ResourceNotFoundException;
import com.itpark.trello.model.Board;
import com.itpark.trello.model.User;
import com.itpark.trello.repository.BoardRepository;
import com.itpark.trello.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    // Создание новой доски
    @Transactional
    public BoardDto createBoard(CreateBoardRequest request, Long ownerId) {
        // Находим владельца
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + ownerId));

        // Создаем доску
        Board board = new Board();
        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());
        board.setOwner(owner);

        // Добавляем владельца в список участников
        board.getMembers().add(owner);

        // Сохраняем
        Board savedBoard = boardRepository.save(board);
        return mapToDto(savedBoard);
    }

    // Получить все доски пользователя (где он владелец)
    public List<BoardDto> getUserBoards(Long userId) {
        return boardRepository.findByOwnerId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Получить доску по ID
    public BoardDto getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Доска не найдена с id: " + id));
        return mapToDto(board);
    }

    // Добавить участника в доску
    @Transactional
    public void addMemberToBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Доска не найдена с id: " + boardId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + userId));

        // Проверяем, не является ли пользователь уже участником
        if (!board.getMembers().contains(user)) {
            board.getMembers().add(user);
            boardRepository.save(board);
        }
    }

    // Удалить участника из доски
    @Transactional
    public void removeMemberFromBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Доска не найдена с id: " + boardId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + userId));

        // Нельзя удалить владельца
        if (board.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Нельзя удалить владельца доски");
        }

        board.getMembers().remove(user);
        boardRepository.save(board);
    }

    // Обновить доску
    @Transactional
    public BoardDto updateBoard(Long boardId, CreateBoardRequest request, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Доска не найдена с id: " + boardId));

        // Проверяем, что пользователь имеет право редактировать (владелец)
        if (!board.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Только владелец может редактировать доску");
        }

        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());

        Board updatedBoard = boardRepository.save(board);
        return mapToDto(updatedBoard);
    }

    // Удалить доску
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Доска не найдена с id: " + boardId));

        // Проверяем, что пользователь имеет право удалить (владелец)
        if (!board.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Только владелец может удалить доску");
        }

        boardRepository.delete(board);
    }

    // Преобразование Entity в DTO
    private BoardDto mapToDto(Board board) {
        BoardDto dto = new BoardDto();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setDescription(board.getDescription());
        dto.setOwner(userService.mapToDto(board.getOwner()));
        dto.setMembers(board.getMembers().stream()
                .map(userService::mapToDto)
                .collect(Collectors.toList()));
        dto.setCreatedAt(board.getCreatedAt());
        return dto;
    }
}
