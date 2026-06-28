package com.itpark.trello.controller;

import com.itpark.trello.dto.CreateTaskRequest;
import com.itpark.trello.dto.TaskDto;
import com.itpark.trello.dto.UpdateTaskPositionRequest;
import com.itpark.trello.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}/columns/{columnId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // Создать задачу в колонке
    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @PathVariable Long boardId,
            @PathVariable Long columnId,
            @Valid @RequestBody CreateTaskRequest request) {
        TaskDto task = taskService.createTask(columnId, request);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    // Получить все задачи в колонке
    @GetMapping
    public ResponseEntity<List<TaskDto>> getColumnTasks(@PathVariable Long columnId) {
        return ResponseEntity.ok(taskService.getColumnTasks(columnId));
    }

    // Получить задачу по ID
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    // Обновить задачу
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    // Удалить задачу
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    // Переместить задачу (drag-and-drop)
    @PatchMapping("/{taskId}/position")
    public ResponseEntity<TaskDto> updateTaskPosition(
            @PathVariable Long boardId,
            @PathVariable Long columnId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskPositionRequest request) {
        TaskDto task = taskService.updateTaskPosition(
                taskId,
                request.getDestinationColumnId(),
                request.getNewPosition()
        );
        return ResponseEntity.ok(task);
    }
}