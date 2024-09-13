package ua.spro.todolist.model.dto;

public record FileAttachmentDto(
    String fileName,
    byte[] data,
    String contentType
) {}
