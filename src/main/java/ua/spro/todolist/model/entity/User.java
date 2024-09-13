package ua.spro.todolist.model.entity;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Task> tasks;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    User user = (User) o;
    return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(
        password, user.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, password);
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", password='" + password + '\'' +
        ", tasks=" + tasks +
        '}';
  }
}
