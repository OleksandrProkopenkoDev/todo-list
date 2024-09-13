package ua.spro.todolist.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.spro.todolist.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByUsername(String username);

  Optional<User> findByUsername(String username);
}
