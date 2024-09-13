package ua.spro.todolist.service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.spro.todolist.mapper.TaskMapper;
import ua.spro.todolist.model.dto.CreateTaskRequest;
import ua.spro.todolist.model.dto.TaskDto;
import ua.spro.todolist.model.dto.UpdateTaskRequest;
import ua.spro.todolist.model.entity.FileAttachment;
import ua.spro.todolist.model.entity.Task;
import ua.spro.todolist.model.entity.User;
import ua.spro.todolist.repository.FileRepository;
import ua.spro.todolist.repository.TaskRepository;
import ua.spro.todolist.repository.UserRepository;
import ua.spro.todolist.service.TaskService;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final FileRepository fileRepository;

  @Override
  @Transactional
  public TaskDto createTask(CreateTaskRequest taskDto) {
    User currentUser = getCurrentUser(); // Method to get the logged-in user
    Task task = TaskMapper.toEntity(taskDto);

    Set<FileAttachment> fileAttachments = extractFileAttachments(taskDto.attachments(), task);
    task.setUser(currentUser);
    task.setAttachments(fileAttachments);

    // Save task
    Task savedTask = taskRepository.save(task);
    return TaskMapper.toDto(savedTask);
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
  public TaskDto updateTask(Long taskId, UpdateTaskRequest request) {
    Task task =
        taskRepository
            .findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task %s not found".formatted(taskId)));
    // Remove existing attachments
    Set<FileAttachment> oldAttachments = new HashSet<>(task.getAttachments());
    task.getAttachments().clear(); // This marks the attachments for deletion
    fileRepository.deleteAll(oldAttachments); // Explicitly delete the old attachments

    updateFromRequest(request, task);

    fileRepository.saveAll(task.getAttachments());

    task = taskRepository.save(task);

    return TaskMapper.toDto(task);
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

  private void updateFromRequest(UpdateTaskRequest request, Task task) {
    Set<FileAttachment> fileAttachments = extractFileAttachments(request.attachments(), task);
    task.setAttachments(fileAttachments);

    // Update task details
    if (request.title() != null) {
      task.setTitle(request.title());
    }
    if (request.description() != null) {
      task.setDescription(request.description());
    }
    if (request.dueDate() != null) {
      task.setDueDate(request.dueDate());
    }
    if (request.completed() != null) {
      task.setCompleted(request.completed());
    }
  }

  private Set<FileAttachment> extractFileAttachments(Set<MultipartFile> attachments, Task task) {
    if (attachments == null) {
      return new HashSet<>();
    }

    return attachments.stream()
        .map(
            multipartFile -> {
              try {
                return FileAttachment.builder()
                    .data(multipartFile.getBytes())
                    .contentType(multipartFile.getContentType())
                    .fileName(multipartFile.getOriginalFilename())
                    .task(task)
                    .build();
              } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
              }
            })
        .collect(Collectors.toSet());
  }
}
