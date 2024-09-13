package ua.spro.todolist.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.spro.todolist.model.entity.User;
import ua.spro.todolist.repository.UserRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataLoader {

  private final PasswordEncoder passwordEncoder;

  @Bean
  public CommandLineRunner demo(UserRepository userRepository) {
    return (args) -> {
      // Check if a user already exists
      if (userRepository.findByUsername("admin").isEmpty()) {
        // Create a new user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("password"));
        userRepository.save(admin);
      }
      log.info("Created Admin user: {}", userRepository.findByUsername("admin").get().getUsername());
    };
  }
}
