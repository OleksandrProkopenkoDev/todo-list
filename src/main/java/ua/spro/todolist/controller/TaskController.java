package ua.spro.todolist.controller;

import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.spro.todolist.model.dto.CreateTaskRequest;
import ua.spro.todolist.model.dto.TaskDto;
import ua.spro.todolist.model.dto.UpdateTaskRequest;
import ua.spro.todolist.service.TaskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
public class TaskController {

  private final TaskService taskService;

  @PostMapping
  public TaskDto createTask(@ModelAttribute CreateTaskRequest taskDto) {
    return taskService.createTask(taskDto);
  }

  @GetMapping
  public Set<TaskDto> getTasks(@RequestParam Map<String, String> params) {
    return taskService.viewTasksWithFilters(params);
  }

  @PutMapping("/{taskId}")
  public TaskDto updateTask(@PathVariable Long taskId, @ModelAttribute UpdateTaskRequest request) {
    return taskService.updateTask(taskId, request);
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
    taskService.deleteTask(taskId);
    return ResponseEntity.noContent().build();
  }
}
