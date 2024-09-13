package ua.spro.todolist.service;

import java.util.Set;
import org.springframework.transaction.annotation.Transactional;
import ua.spro.todolist.model.dto.TaskDto;

public interface TaskService {

  @Transactional
  void createTask(TaskDto taskDto);

  @Transactional(readOnly = true)
  Set<TaskDto> findTasksByUser();

  @Transactional
  void updateTask(Long taskId, TaskDto taskDto);

  @Transactional
  void deleteTask(Long taskId);
}
