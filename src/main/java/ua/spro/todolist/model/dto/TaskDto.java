package ua.spro.todolist.model.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record TaskDto(
    String title,
    String description,
    LocalDateTime dueDate,
    boolean completed,
    Set<FileAttachmentDto> attachments
) {}
