package ua.spro.todolist.service;

import java.util.Set;
import ua.spro.todolist.model.dto.CreateTaskRequest;
import ua.spro.todolist.model.dto.TaskDto;
import ua.spro.todolist.model.dto.UpdateTaskRequest;

public interface TaskService {

  TaskDto createTask(CreateTaskRequest taskDto);

  Set<TaskDto> findTasksByUser();

  TaskDto updateTask(Long taskId, UpdateTaskRequest request);

  void deleteTask(Long taskId);
}
