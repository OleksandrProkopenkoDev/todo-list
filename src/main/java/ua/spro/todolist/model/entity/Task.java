package ua.spro.todolist.model.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  private String description;

  private LocalDateTime dueDate;

  private boolean completed;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  private Set<FileAttachment> attachments = new HashSet<>();

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Task task = (Task) o;
    return completed == task.completed && Objects.equals(id, task.id) && Objects.equals(title,
        task.title) && Objects.equals(description, task.description) && Objects.equals(dueDate,
        task.dueDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, description, dueDate, completed);
  }

  @Override
  public String toString() {
    return "Task{"
        + "attachments="
        + attachments.stream().map(FileAttachment::getFileName).toList()
        + ", id="
        + id
        + ", title='"
        + title
        + '\''
        + ", description='"
        + description
        + '\''
        + ", dueDate="
        + dueDate
        + ", completed="
        + completed
        + ", user="
        + user.getUsername()
        + '}';
  }
}
