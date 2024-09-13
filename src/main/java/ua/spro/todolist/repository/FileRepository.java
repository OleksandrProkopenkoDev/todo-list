package ua.spro.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.spro.todolist.model.entity.FileAttachment;

public interface FileRepository extends JpaRepository<FileAttachment, Long> {}
