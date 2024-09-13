package ua.spro.todolist.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import ua.spro.todolist.model.dto.TaskDto;
import ua.spro.todolist.model.entity.Task;

public class TaskMapper {

  public static Task toEntity(TaskDto taskDto) {
    return Task.builder()
        .title(taskDto.title())
        .description(taskDto.description())
        .dueDate(taskDto.dueDate())
        .completed(taskDto.completed())
        .attachments(
            taskDto.attachments().stream()
                .map(FileAttachmentMapper::toEntity)
                .collect(Collectors.toSet()))
        .build();
  }

  public static TaskDto toDto(Task task) {
    return new TaskDto(
        task.getTitle(),
        task.getDescription(),
        task.getDueDate(),
        task.isCompleted(),
        task.getAttachments().stream()
            .map(FileAttachmentMapper::toDto)
            .collect(Collectors.toSet()));
  }

  public static Set<TaskDto> toDtoSet(Set<Task> tasks) {
    return tasks.stream().map(TaskMapper::toDto).collect(Collectors.toSet());
  }
}
