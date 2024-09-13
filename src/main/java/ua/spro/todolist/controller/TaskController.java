package ua.spro.todolist.controller;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.spro.todolist.model.dto.TaskDto;
import ua.spro.todolist.service.TaskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {

  private final TaskService taskService;

  @PostMapping
  public ResponseEntity<String> createTask(@RequestBody TaskDto taskDto) {
    taskService.createTask(taskDto);
    return ResponseEntity.ok("Task created successfully");
  }

  @GetMapping
  public ResponseEntity<Set<TaskDto>> viewTasks() {
    Set<TaskDto> tasks = taskService.findTasksByUser();
    return ResponseEntity.ok(tasks);
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<String> updateTask(
      @PathVariable Long taskId, @RequestBody TaskDto taskDto) {
    taskService.updateTask(taskId, taskDto);
    return ResponseEntity.ok("Task updated successfully");
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
    taskService.deleteTask(taskId);
    return ResponseEntity.ok("Task deleted successfully");
  }

  @GetMapping("/demo")
  public String demo() {
    return "Hello World";
  }
}
