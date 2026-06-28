package com.itpark.trello.service;

import com.itpark.trello.dto.BoardDto;
import com.itpark.trello.dto.ColumnDto;
import com.itpark.trello.dto.CreateBoardRequest;
import com.itpark.trello.exception.ResourceNotFoundException;
import com.itpark.trello.model.Board;
import com.itpark.trello.model.BoardColumn;
import com.itpark.trello.model.User;
import com.itpark.trello.repository.BoardColumnRepository;
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
    private final BoardColumnRepository columnRepository;

    @Transactional
    public BoardDto createBoard(CreateBoardRequest request, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + ownerId));

        Board board = new Board();
        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());
        board.setOwner(owner);
        board.getMembers().add(owner);

        Board savedBoard = boardRepository.save(board);
        return mapToDto(savedBoard);
    }

    public List<BoardDto> getUserBoards(Long userId) {
        return boardRepository.findByOwnerId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BoardDto getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Доска не найдена с id: " + id));

        List<BoardColumn> columns = columnRepository.findByBoardIdOrderByPositionAsc(id);
        board.setColumns(columns);

        return mapToDto(board);
    }

    @Transactional
    public void addMemberToBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Доска не найдена с id: " + boardId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + userId));

        if (!board.getMembers().contains(user)) {
            board.getMembers().add(user);
            boardRepository.save(board);
        }
    }

    @Transactional
    public void removeMemberFromBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Доска не найдена с id: " + boardId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + userId));

        if (board.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Нельзя удалить владельца доски");
        }

        board.getMembers().remove(user);
        boardRepository.save(board);
    }

    @Transactional
    public BoardDto updateBoard(Long boardId, CreateBoardRequest request, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Доска не найдена с id: " + boardId));

        if (!board.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Только владелец может редактировать доску");
        }

        board.setTitle(request.getTitle());
        board.setDescription(request.getDescription());

        Board updatedBoard = boardRepository.save(board);
        return mapToDto(updatedBoard);
    }

    @Transactional
    public void deleteBoard(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Доска не найдена с id: " + boardId));

        if (!board.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Только владелец может удалить доску");
        }

        boardRepository.delete(board);
    }

    private BoardDto mapToDto(Board board) {
        BoardDto dto = new BoardDto();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setDescription(board.getDescription());
        dto.setOwner(userService.mapToDto(board.getOwner()));
        dto.setMembers(board.getMembers().stream()
                .map(userService::mapToDto)
                .collect(Collectors.toList()));

        // ✅ ДОБАВЛЯЕМ КОЛОНКИ (используем mapColumnToDto, который возвращает ColumnDto)
        if (board.getColumns() != null) {
            dto.setColumns(board.getColumns().stream()
                    .map(this::mapColumnToDto)
                    .collect(Collectors.toList()));
        }

        dto.setCreatedAt(board.getCreatedAt());
        return dto;
    }

    // ✅ ЭТОТ МЕТОД ВОЗВРАЩАЕТ ColumnDto
    private ColumnDto mapColumnToDto(BoardColumn column) {
        ColumnDto dto = new ColumnDto();
        dto.setId(column.getId());
        dto.setTitle(column.getTitle());
        dto.setPosition(column.getPosition());
        return dto;
    }
}