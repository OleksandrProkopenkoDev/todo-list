package ua.spro.todolist.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import ua.spro.todolist.model.dto.CreateTaskRequest;
import ua.spro.todolist.model.dto.TaskDto;
import ua.spro.todolist.model.dto.UpdateTaskRequest;

public interface TaskService {

  TaskDto createTask(CreateTaskRequest taskDto);

  Set<TaskDto> viewTasksWithFilters(Map<String, String> params);

  TaskDto updateTask(Long taskId, UpdateTaskRequest request);

  void deleteTask(Long taskId);

  List<byte[]> getFileAttachmentsByTask(Long taskId);

  TaskDto getTaskById(Long taskId);
}
