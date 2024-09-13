package ua.spro.todolist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import ua.spro.todolist.service.TaskService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/task")
public class AttachmentController {

  private final TaskService taskService;

  @GetMapping("/{taskId}/attachment")
  public List<byte[]> getAttachments(@PathVariable("taskId") Long taskId) {
    return taskService.getFileAttachmentsByTask(taskId);
  }
}
