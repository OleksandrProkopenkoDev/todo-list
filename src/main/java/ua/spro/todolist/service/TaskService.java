package ua.spro.todolist.service;

import java.util.Set;
import ua.spro.todolist.model.dto.CreateTaskRequest;
import ua.spro.todolist.model.dto.TaskDto;

public interface TaskService {

  TaskDto createTask(CreateTaskRequest taskDto);

  Set<TaskDto> findTasksByUser();

  void updateTask(Long taskId, TaskDto taskDto);

  void deleteTask(Long taskId);
}
