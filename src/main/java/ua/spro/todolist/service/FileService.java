package ua.spro.todolist.service;

import ua.spro.todolist.model.dto.FileAttachmentDto;

public interface FileService {

  void uploadFile(FileAttachmentDto fileAttachmentDTO);
}
