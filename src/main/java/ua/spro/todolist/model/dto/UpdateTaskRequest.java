package ua.spro.todolist.model.dto;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public record UpdateTaskRequest (
    String title,
    String description,
    LocalDateTime dueDate,
    Boolean completed,
    Set<MultipartFile> attachments
) {}
