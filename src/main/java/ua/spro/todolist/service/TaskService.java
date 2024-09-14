package ua.spro.todolist.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import ua.spro.todolist.model.dto.TaskRequest;
import ua.spro.todolist.model.dto.TaskDto;

public interface TaskService {

  TaskDto createTask(TaskRequest taskDto);

  Set<TaskDto> viewTasksWithFilters(Map<String, String> params);

  TaskDto updateTask(Long taskId, TaskRequest request);

  void deleteTask(Long taskId);

  List<byte[]> getFileAttachmentsByTask(Long taskId);

  TaskDto getTaskById(Long taskId);
}
