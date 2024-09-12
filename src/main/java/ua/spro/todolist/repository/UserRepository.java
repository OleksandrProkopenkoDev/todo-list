package ua.spro.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.spro.todolist.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {}
