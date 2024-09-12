package ua.spro.todolist.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileAttachment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String fileName;

  @Lob
  private byte[] data;

  @Column(nullable = false)
  private String contentType;

  @ManyToOne
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;
}

