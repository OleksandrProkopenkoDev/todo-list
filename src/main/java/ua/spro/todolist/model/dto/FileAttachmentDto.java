package ua.spro.todolist.model.dto;

public record FileAttachmentDto(
    Long id,
    String fileName,
    String contentType
) {}
