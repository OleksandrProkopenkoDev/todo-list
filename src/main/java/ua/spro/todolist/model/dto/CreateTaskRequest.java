package ua.spro.todolist.model.dto;

import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public record CreateTaskRequest (
    String title,
    String description,
    LocalDateTime dueDate,
    boolean completed,
    Set<MultipartFile> attachments
) {}
