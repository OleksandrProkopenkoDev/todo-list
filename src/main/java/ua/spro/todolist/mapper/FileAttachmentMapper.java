package ua.spro.todolist.mapper;

import ua.spro.todolist.model.dto.FileAttachmentDto;
import ua.spro.todolist.model.entity.FileAttachment;

public class FileAttachmentMapper {

  public static FileAttachmentDto toDto(FileAttachment file) {
    return new FileAttachmentDto(file.getFileName(), file.getData(), file.getContentType());
  }

  public static FileAttachment toEntity(FileAttachmentDto fileDto) {
    return FileAttachment.builder()
        .fileName(fileDto.fileName())
        .data(fileDto.data())
        .contentType(fileDto.contentType())
        .build();
  }
}
