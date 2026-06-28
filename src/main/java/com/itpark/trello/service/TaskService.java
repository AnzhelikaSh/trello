package com.itpark.trello.service;

import com.itpark.trello.dto.CreateTaskRequest;
import com.itpark.trello.dto.TaskDto;
import com.itpark.trello.dto.UserDto;
import com.itpark.trello.exception.ResourceNotFoundException;
import com.itpark.trello.model.BoardColumn;
import com.itpark.trello.model.Task;
import com.itpark.trello.model.User;
import com.itpark.trello.repository.BoardColumnRepository;
import com.itpark.trello.repository.TaskRepository;
import com.itpark.trello.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    // Создать задачу в колонке
    @Transactional
    public TaskDto createTask(Long columnId, CreateTaskRequest request) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Колонка не найдена с id: " + columnId));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setColumn(column);

        // Устанавливаем позицию (последняя + 1)
        int position = taskRepository.findByColumnIdOrderByPositionAsc(columnId).size();
        task.setPosition(position);

        // Устанавливаем исполнителя, если указан
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + request.getAssigneeId()));
            task.setAssignee(assignee);
        }

        Task savedTask = taskRepository.save(task);
        return mapToDto(savedTask);
    }

    // Получить все задачи в колонке
    public List<TaskDto> getColumnTasks(Long columnId) {
        return taskRepository.findByColumnIdOrderByPositionAsc(columnId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Получить задачу по ID
    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Задача не найдена с id: " + taskId));
        return mapToDto(task);
    }

    // Обновить задачу
    @Transactional
    public TaskDto updateTask(Long taskId, CreateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Задача не найдена с id: " + taskId));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        // Обновляем исполнителя, если указан
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден с id: " + request.getAssigneeId()));
            task.setAssignee(assignee);
        } else {
            task.setAssignee(null); // Снимаем исполнителя
        }

        Task updatedTask = taskRepository.save(task);
        return mapToDto(updatedTask);
    }

    // Удалить задачу
    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Задача не найдена с id: " + taskId));

        taskRepository.delete(task);
    }

    // Переместить задачу в другую колонку или изменить позицию
    @Transactional
    public TaskDto updateTaskPosition(Long taskId, Long destinationColumnId, Integer newPosition) {
        // 1. Находим задачу
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Задача не найдена с id: " + taskId));

        Long oldColumnId = task.getColumn().getId();

        // 2. Если задача перемещается в другую колонку
        if (!oldColumnId.equals(destinationColumnId)) {
            BoardColumn newColumn = columnRepository.findById(destinationColumnId)
                    .orElseThrow(() -> new ResourceNotFoundException("Колонка не найдена с id: " + destinationColumnId));
            task.setColumn(newColumn);
        }

        // 3. Обновляем позицию
        task.setPosition(newPosition);

        // 4. Пересчитываем позиции в старой колонке
        if (!oldColumnId.equals(destinationColumnId)) {
            reorderTasksInColumn(oldColumnId);
        }

        // 5. Пересчитываем позиции в новой колонке
        reorderTasksInColumn(destinationColumnId);

        Task updatedTask = taskRepository.save(task);
        return mapToDto(updatedTask);
    }

    // Вспомогательный метод для пересчета позиций задач в колонке
    private void reorderTasksInColumn(Long columnId) {
        if (columnId == null) {
            return;
        }
        List<Task> tasks = taskRepository.findByColumnIdOrderByPositionAsc(columnId);
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition(i);
        }
        taskRepository.saveAll(tasks);
    }

    // Преобразование Entity в DTO
    private TaskDto mapToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setPosition(task.getPosition());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        if (task.getAssignee() != null) {
            dto.setAssignee(userService.mapToDto(task.getAssignee()));
        }

        return dto;
    }
}