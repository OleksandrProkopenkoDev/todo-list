package ua.spro.todolist.mapper;

import ua.spro.todolist.model.dto.FileAttachmentDto;
import ua.spro.todolist.model.entity.FileAttachment;

public class FileAttachmentMapper {

  public static FileAttachmentDto toDto(FileAttachment file) {
    return new FileAttachmentDto(file.getId(), file.getFileName(), file.getContentType());
  }

  public static FileAttachment toEntity(FileAttachmentDto fileDto) {
    return FileAttachment.builder()
        .id(fileDto.id())
        .fileName(fileDto.fileName())
        .contentType(fileDto.contentType())
        .build();
  }
}
