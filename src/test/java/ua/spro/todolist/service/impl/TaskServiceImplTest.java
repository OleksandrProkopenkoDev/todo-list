package ua.spro.todolist.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;
import ua.spro.todolist.model.dto.CreateTaskRequest;
import ua.spro.todolist.model.dto.FileAttachmentDto;
import ua.spro.todolist.model.dto.TaskDto;
import ua.spro.todolist.model.dto.UpdateTaskRequest;
import ua.spro.todolist.model.entity.FileAttachment;
import ua.spro.todolist.model.entity.Task;
import ua.spro.todolist.model.entity.User;
import ua.spro.todolist.repository.FileRepository;
import ua.spro.todolist.repository.TaskRepository;
import ua.spro.todolist.repository.UserRepository;
import ua.spro.todolist.specification.TaskSpecification;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

  @Mock private TaskRepository taskRepository;

  @Mock private UserRepository userRepository;

  @Mock private FileRepository fileRepository;

  @InjectMocks private TaskServiceImpl taskService;

  @BeforeEach
  public void setUp() {
    // Set up SecurityContextHolder for mocking the user
    UserDetails userDetails = mock(UserDetails.class);
    lenient().when(userDetails.getUsername()).thenReturn("testUser");
    Authentication authentication = mock(Authentication.class);
    lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  @Nested
  class CreateTask {

    @Test
    public void createTask_shouldCreateTask_whenValidRequest() {
      // Arrange
      CreateTaskRequest createTaskRequest =
          new CreateTaskRequest("Task Title", "Task Description", null, false, null);
      User user = new User(1L, "testUser", "password", new HashSet<>());

      Task savedTask =
          new Task(1L, "Task Title", "Task Description", null, false, user, new HashSet<>());
      TaskDto taskDto = new TaskDto(1L, "Task Title", "Task Description", null, false, null);

      when(userRepository.findByUsername("testUser")).thenReturn(java.util.Optional.of(user));
      when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

      // Act
      TaskDto result = taskService.createTask(createTaskRequest);

      // Assert
      assertNotNull(result);
      assertEquals(taskDto.id(), result.id());
      assertEquals(taskDto.title(), result.title());
      verify(taskRepository).save(any(Task.class));
    }

    @Test
    public void createTask_shouldThrowException_whenUserNotFound() {
      CreateTaskRequest createTaskRequest =
          new CreateTaskRequest("Task Title", "Task Description", null, false, null);

      when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

      RuntimeException thrown =
          assertThrows(RuntimeException.class, () -> taskService.createTask(createTaskRequest));
      assertTrue(thrown.getMessage().contains("User not found"));
    }

    @Test
    public void createTask_shouldThrowException_whenIOExceptionOccurs() throws IOException {
      MultipartFile file = mock(MultipartFile.class);
      when(file.getBytes()).thenThrow(new IOException("File read error"));

      CreateTaskRequest createTaskRequest =
          new CreateTaskRequest("Task Title", "Task Description", null, false, Set.of(file));
      User user = new User(1L, "testUser", "password", new HashSet<>());

      when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

      RuntimeException thrown =
          assertThrows(RuntimeException.class, () -> taskService.createTask(createTaskRequest));
      assertTrue(thrown.getMessage().contains("File read error"));
    }

    @Test
    public void createTask_shouldThrowException_whenCreateTaskRequestIsNull() {
      assertThrows(RuntimeException.class, () -> taskService.createTask(null));
    }
  }

  @Nested
  class ViewTasksWithFilters {

    @Test
    public void viewTasksWithFilters_shouldReturnTasks_whenFiltersMatch() {
      // Arrange
      User user = new User(1L, "testUser", "password", new HashSet<>());
      Task task =
          new Task(1L, "Task Title", "Task Description", null, false, user, new HashSet<>());
      TaskDto taskDto =
          new TaskDto(1L, "Task Title", "Task Description", null, false, new HashSet<>());

      Map<String, String> filters = Map.of("completed", "false");
      Specification<Task> specification =
          Specification.where(TaskSpecification.filterByParams(filters))
              .and((root, query, cb) -> cb.equal(root.get("user"), user));

      when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
      when(taskRepository.findAll(any(Specification.class))).thenReturn(List.of(task));

      // Act
      Set<TaskDto> result = taskService.viewTasksWithFilters(filters);

      // Assert
      assertNotNull(result);
      assertEquals(1, result.size());
      assertTrue(result.contains(taskDto));
      verify(taskRepository).findAll(any(Specification.class));
    }

    @Test
    public void viewTasksWithFilters_shouldReturnEmptySet_whenNoTasksMatch() {
      // Arrange
      User user = new User(1L, "testUser", "password", new HashSet<>());
      Map<String, String> filters = Map.of("completed", "true");

      Specification<Task> specification =
          Specification.where(TaskSpecification.filterByParams(filters))
              .and((root, query, cb) -> cb.equal(root.get("user"), user));

      when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
      when(taskRepository.findAll(any(Specification.class))).thenReturn(List.of());

      // Act
      Set<TaskDto> result = taskService.viewTasksWithFilters(filters);

      // Assert
      assertNotNull(result);
      assertTrue(result.isEmpty());
      verify(taskRepository).findAll(any(Specification.class));
    }
  }

  @Nested
  class UpdateTask {
    @Test
    public void updateTask_shouldUpdateTask_whenValidData() {
      // Arrange
      User user = new User(1L, "testUser", "password", new HashSet<>());
      Task existingTask =
          new Task(1L, "Old Title", "Old Description", null, false, user, new HashSet<>());
      user.getTasks().add(existingTask);
      FileAttachment fileAttachment =
          new FileAttachment(1L, "application/pdf", new byte[] {1, 2, 3}, "file.pdf", existingTask);
      FileAttachmentDto fileAttachmentDto =
          new FileAttachmentDto(1L, "file.pdf", "application/pdf");

      UpdateTaskRequest updateRequest =
          new UpdateTaskRequest(
              "New Title", "New Description", null, true, Set.of(mock(MultipartFile.class)));

      Task updatedTask =
          new Task(1L, "New Title", "New Description", null, true, user, Set.of(fileAttachment));
      TaskDto taskDto =
          new TaskDto(1L, "New Title", "New Description", null, true, Set.of(fileAttachmentDto));

      when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
      when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
      when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
      when(fileRepository.saveAll(any(Set.class))).thenReturn(List.of(fileAttachment));

      // Act
      TaskDto result = taskService.updateTask(1L, updateRequest);

      // Assert
      assertNotNull(result);
      assertEquals(taskDto.id(), result.id());
      assertEquals(taskDto.title(), result.title());
      verify(taskRepository).save(any(Task.class));
      verify(fileRepository).deleteAll(any(Set.class));
      verify(fileRepository).saveAll(any(Set.class));
    }

    @Test
    public void updateTask_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
      // Arrange
      User anotherUser = new User(2L, "anotherUser", "password", new HashSet<>());
      Task existingTask =
          new Task(1L, "Old Title", "Old Description", null, false, anotherUser, new HashSet<>());
      UpdateTaskRequest updateRequest =
          new UpdateTaskRequest(
              "New Title", "New Description", null, true, Set.of(mock(MultipartFile.class)));

      when(userRepository.findByUsername("testUser"))
          .thenReturn(Optional.of(new User(1L, "testUser", "password", new HashSet<>())));
      when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

      // Act & Assert
      AccessDeniedException thrown =
          assertThrows(
              AccessDeniedException.class, () -> taskService.updateTask(1L, updateRequest));
      assertTrue(
          thrown
              .getMessage()
              .contains("You do not have permission to manage this task with id: 1"));
    }

    @Test
    public void updateTask_shouldThrowRuntimeException_whenTaskNotFound() {
      // Arrange
      UpdateTaskRequest updateRequest =
          new UpdateTaskRequest(
              "New Title", "New Description", null, true, Set.of(mock(MultipartFile.class)));

      when(taskRepository.findById(1L)).thenReturn(Optional.empty());

      // Act & Assert
      RuntimeException thrown =
          assertThrows(RuntimeException.class, () -> taskService.updateTask(1L, updateRequest));

      assertTrue(thrown.getMessage().contains("Task 1 not found"));
    }

    @Test
    public void updateTask_shouldHandleAttachmentUpdate_whenNewAttachmentsProvided()
        throws IOException {
      // Arrange
      User user = new User(1L, "testUser", "password", new HashSet<>());
      Task existingTask =
          new Task(1L, "Old Title", "Old Description", null, false, user, new HashSet<>());
      user.getTasks().add(existingTask);
      MultipartFile newAttachmentFile = mock(MultipartFile.class);
      when(newAttachmentFile.getBytes()).thenReturn(new byte[] {4, 5, 6});
      when(newAttachmentFile.getContentType()).thenReturn("application/pdf");
      when(newAttachmentFile.getOriginalFilename()).thenReturn("new-file.pdf");
      UpdateTaskRequest updateRequest =
          new UpdateTaskRequest(
              "New Title", "New Description", null, true, Set.of(newAttachmentFile));

      FileAttachment newFileAttachment =
          new FileAttachment(
              2L, "application/pdf", new byte[] {4, 5, 6}, "new-file.pdf", existingTask);
      FileAttachmentDto fileAttachmentDto =
          new FileAttachmentDto(1L, "file.pdf", "application/pdf");
      Task updatedTask =
          new Task(1L, "New Title", "New Description", null, true, user, Set.of(newFileAttachment));
      TaskDto taskDto =
          new TaskDto(1L, "New Title", "New Description", null, true, Set.of(fileAttachmentDto));

      when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
      when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
      when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
      when(fileRepository.saveAll(any(Set.class))).thenReturn(List.of(newFileAttachment));
      // when(fileRepository.deleteAll(any())).thenReturn(null);

      // Act
      TaskDto result = taskService.updateTask(1L, updateRequest);

      // Assert
      assertNotNull(result);
      assertEquals(taskDto.id(), result.id());
      assertEquals(taskDto.title(), result.title());
      verify(taskRepository).save(any(Task.class));
      verify(fileRepository).deleteAll(any(Set.class));
      verify(fileRepository).saveAll(any(Set.class));
    }
  }

  @Nested
  class DeleteTask {
    @Test
    public void deleteTask_shouldDeleteTask_whenUserIsOwner() {
      // Arrange
      User user = new User(1L, "testUser", "password", new HashSet<>());
      FileAttachment fileAttachment =
          new FileAttachment(1L, "application/pdf", new byte[] {1, 2, 3}, "file.pdf", null);
      Task existingTask =
          new Task(
              1L,
              "Task Title",
              "Task Description",
              null,
              false,
              user,
              new HashSet<>(Set.of(fileAttachment)));
      user.getTasks().add(existingTask);
      when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
      when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

      // Act
      taskService.deleteTask(1L);

      // Assert
      verify(taskRepository).delete(existingTask);
    }

    @Test
    public void deleteTask_shouldThrowAccessDeniedException_whenUserIsNotOwner() {
      // Arrange
      User anotherUser = new User(2L, "anotherUser", "password", new HashSet<>());
      Task existingTask =
          new Task(1L, "Task Title", "Task Description", null, false, anotherUser, new HashSet<>());
      when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
      when(userRepository.findByUsername("testUser"))
          .thenReturn(Optional.of(new User(1L, "testUser", "password", new HashSet<>())));

      // Act & Assert
      AccessDeniedException thrown =
          assertThrows(AccessDeniedException.class, () -> taskService.deleteTask(1L));
      assertTrue(
          thrown
              .getMessage()
              .contains("You do not have permission to manage this task with id: 1"));
    }

    @Test
    public void deleteTask_shouldThrowRuntimeException_whenTaskNotFound() {
      // Arrange

      when(taskRepository.findById(1L)).thenReturn(Optional.empty());

      // Act & Assert
      RuntimeException thrown =
          assertThrows(RuntimeException.class, () -> taskService.deleteTask(1L));
      assertTrue(thrown.getMessage().contains("Task 1 not found"));
    }

    @Test
    public void deleteTask_shouldHandleTaskWithAttachments_whenAttachmentsExist() {
      // Arrange
      User user = new User(1L, "testUser", "password", new HashSet<>());
      FileAttachment fileAttachment =
          new FileAttachment(1L, "application/pdf", new byte[] {1, 2, 3}, "file.pdf", null);
      Task existingTask =
          new Task(
              1L,
              "Task Title",
              "Task Description",
              null,
              false,
              user,
              new HashSet<>(Set.of(fileAttachment)));
      user.getTasks().add(existingTask);
      when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
      when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

      // Act
      taskService.deleteTask(1L);

      // Assert
      verify(taskRepository).delete(existingTask);
    }
  }

  @Nested
  class GetFileAttachmentsByTask {
    @Test
    public void getFileAttachmentsByTask_shouldReturnAttachments_whenUserOwnsTask() {
      // Arrange
      User user = new User(1L, "testUser", "password", new HashSet<>());
      byte[] expectedArray1 = {1, 2, 3};
      FileAttachment fileAttachment1 =
          new FileAttachment(1L, "image/jpeg", expectedArray1, "file1.jpg", null);
      byte[] expectedArray2 = {4, 5, 6};
      FileAttachment fileAttachment2 =
          new FileAttachment(2L, "image/png", expectedArray2, "file2.png", null);
      Set<FileAttachment> attachments = new HashSet<>();
      attachments.add(fileAttachment1);
      attachments.add(fileAttachment2);

      Task task = new Task(1L, "Task Title", "Task Description", null, false, user, attachments);
      user.getTasks().add(task);
      fileAttachment1.setTask(task);
      fileAttachment2.setTask(task);
      when(userRepository.findByUsername("testUser")).thenReturn(java.util.Optional.of(user));
      when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

      // Act
      List<byte[]> result = taskService.getFileAttachmentsByTask(1L);

      // Assert
      assertNotNull(result);
      assertEquals(2, result.size());

      assertTrue(result.contains(expectedArray1));
      assertTrue(result.contains(expectedArray2));
    }

    @Test
    public void getFileAttachmentsByTask_shouldThrowAccessDeniedException_whenUserDoesNotOwnTask() {
      // Arrange
      User user = new User(1L, "testUser", "password", new HashSet<>());
      User otherUser = new User(2L, "otherUser", "password", new HashSet<>());
      FileAttachment fileAttachment =
          new FileAttachment(1L, "image/jpeg", new byte[] {1, 2, 3}, "file1.jpg", null);
      Set<FileAttachment> attachments = new HashSet<>();
      attachments.add(fileAttachment);

      Task task =
          new Task(1L, "Task Title", "Task Description", null, false, otherUser, attachments);
      when(userRepository.findByUsername("testUser")).thenReturn(java.util.Optional.of(user));
      when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));

      // Act & Assert
      assertThrows(AccessDeniedException.class, () -> taskService.getFileAttachmentsByTask(1L));
    }

    @Test
    public void getFileAttachmentsByTask_shouldThrowRuntimeException_whenTaskNotFound() {
      // Arrange
      when(taskRepository.findById(1L)).thenReturn(java.util.Optional.empty());

      // Act & Assert
      assertThrows(RuntimeException.class, () -> taskService.getFileAttachmentsByTask(1L));
    }
  }
}
