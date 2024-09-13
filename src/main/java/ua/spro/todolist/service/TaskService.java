package ua.spro.todolist.service;

import java.util.Map;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import ua.spro.todolist.model.dto.CreateTaskRequest;
import ua.spro.todolist.model.dto.TaskDto;
import ua.spro.todolist.model.dto.UpdateTaskRequest;

public interface TaskService {

  TaskDto createTask(CreateTaskRequest taskDto);

  Set<TaskDto> viewTasksWithFilters(Map<String, String> params);

  TaskDto updateTask(Long taskId, UpdateTaskRequest request);

  void deleteTask(Long taskId);
}
