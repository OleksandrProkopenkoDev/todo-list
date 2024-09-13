package ua.spro.todolist.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.spro.todolist.mapper.FileAttachmentMapper;
import ua.spro.todolist.model.dto.FileAttachmentDto;
import ua.spro.todolist.model.entity.FileAttachment;
import ua.spro.todolist.repository.FileRepository;
import ua.spro.todolist.service.FileService;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final FileRepository fileRepository;

  @Transactional
  public void uploadFile(FileAttachmentDto fileDTO) {
    FileAttachment file = FileAttachmentMapper.toEntity(fileDTO);
    fileRepository.save(file);
  }

  @Transactional(readOnly = true)
  public FileAttachmentDto downloadFile(Long fileId) {
    FileAttachment file = fileRepository.findById(fileId)
        .orElseThrow(() -> new RuntimeException("File not found"));

    return FileAttachmentMapper.toDto(file);
  }
}
