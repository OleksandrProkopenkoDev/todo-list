package ua.spro.todolist.model.entity;

import jakarta.persistence.*;
import java.util.Arrays;
import java.util.Objects;
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

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    FileAttachment that = (FileAttachment) o;
    return Objects.equals(id, that.id) && Objects.equals(fileName, that.fileName)
        && Objects.deepEquals(data, that.data) && Objects.equals(contentType, that.contentType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, fileName, Arrays.hashCode(data), contentType);
  }

  @Override
  public String toString() {
    return "FileAttachment{" +
        "contentType='" + contentType + '\'' +
        ", id=" + id +
        ", fileName='" + fileName + '\'' +
        ", data=" + Arrays.toString(data) +
        ", task=" + task.getTitle() +
        '}';
  }
}

