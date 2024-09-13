package ua.spro.todolist.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.spro.todolist.model.dto.TaskDto;
import ua.spro.todolist.mapper.TaskMapper;
import ua.spro.todolist.model.entity.Task;
import ua.spro.todolist.model.entity.User;
import ua.spro.todolist.repository.TaskRepository;
import ua.spro.todolist.repository.UserRepository;
import ua.spro.todolist.service.FileService;
import ua.spro.todolist.service.TaskService;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final FileService fileService;

  @Override
  @Transactional
  public void createTask(TaskDto taskDto) {
    User currentUser = getCurrentUser(); // Method to get the logged-in user
    Task task = TaskMapper.toEntity(taskDto);
    task.setUser(currentUser);

    // Save task
    Task savedTask = taskRepository.save(task);

    // Handle file attachments
    if (taskDto.attachments() != null) {
      taskDto.attachments().forEach(fileService::uploadFile);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Set<TaskDto> findTasksByUser() {
    User currentUser = getCurrentUser(); // Method to get the logged-in user
    Set<Task> tasks = taskRepository.findByUser(currentUser);
    return TaskMapper.toDtoSet(tasks);
  }

  @Override
  @Transactional
  public void updateTask(Long taskId, TaskDto taskDto) {
    Task task =
        taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));

    // Update task details
    task.setTitle(taskDto.title());
    task.setDescription(taskDto.description());
    task.setDueDate(taskDto.dueDate());
    task.setCompleted(taskDto.completed());

    taskRepository.save(task);
  }

  @Override
  @Transactional
  public void deleteTask(Long taskId) {
    Task task =
        taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));

    taskRepository.delete(task);
  }

  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return userRepository
        .findByUsername(userDetails.getUsername())
        .orElseThrow(
            () -> new RuntimeException("User not found %s".formatted(userDetails.getUsername())));
  }
}
