package ua.spro.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.spro.todolist.model.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {}
