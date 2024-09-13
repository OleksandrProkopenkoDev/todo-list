package ua.spro.todolist.repository;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.spro.todolist.model.entity.Task;
import ua.spro.todolist.model.entity.User;

public interface TaskRepository extends JpaRepository<Task, Long> {

  Set<Task> findByUser(User currentUser);
}
