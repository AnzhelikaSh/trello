package com.itpark.trello.service;

import com.itpark.trello.dto.BoardColumnDto;
import com.itpark.trello.dto.CreateColumnRequest;
import com.itpark.trello.exception.ResourceNotFoundException;
import com.itpark.trello.model.Board;
import com.itpark.trello.model.BoardColumn;
import com.itpark.trello.repository.BoardColumnRepository;
import com.itpark.trello.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardColumnService {

    private final BoardColumnRepository columnRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public BoardColumnDto createColumn(Long boardId, CreateColumnRequest request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Доска не найдена с id: " + boardId));

        BoardColumn column = new BoardColumn();
        column.setTitle(request.getTitle());
        column.setBoard(board);

        int position = columnRepository.findByBoardIdOrderByPositionAsc(boardId).size();
        column.setPosition(position);

        BoardColumn savedColumn = columnRepository.save(column);
        return mapToDto(savedColumn);
    }

    public List<BoardColumnDto> getBoardColumns(Long boardId) {
        return columnRepository.findByBoardIdOrderByPositionAsc(boardId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BoardColumnDto getColumnById(Long columnId) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Колонка не найдена с id: " + columnId));
        return mapToDto(column);
    }

    @Transactional
    public BoardColumnDto updateColumn(Long columnId, CreateColumnRequest request) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Колонка не найдена с id: " + columnId));

        column.setTitle(request.getTitle());

        BoardColumn updatedColumn = columnRepository.save(column);
        return mapToDto(updatedColumn);
    }

    @Transactional
    public void deleteColumn(Long columnId) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Колонка не найдена с id: " + columnId));

        columnRepository.delete(column);
    }

    // Изменить порядок колонок
    @Transactional
    public void reorderColumns(Long boardId, List<Long> columnIdsInOrder) {
        List<BoardColumn> columns = columnRepository.findByBoardIdOrderByPositionAsc(boardId);

        for (int i = 0; i < columnIdsInOrder.size(); i++) {
            Long columnId = columnIdsInOrder.get(i);

            // Обычный цикл без лямбды
            for (BoardColumn column : columns) {
                if (column.getId().equals(columnId)) {
                    column.setPosition(i);
                    break;
                }
            }
        }

        columnRepository.saveAll(columns);
    }

    // Исправленный метод mapToDto
    private BoardColumnDto mapToDto(BoardColumn column) {
        BoardColumnDto dto = new BoardColumnDto();
        dto.setId(column.getId());
        dto.setTitle(column.getTitle());  // ← было getName(), исправь на getTitle()
        dto.setPosition(column.getPosition());
        return dto;
    }
}