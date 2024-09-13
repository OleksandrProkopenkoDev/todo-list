package ua.spro.todolist.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.spro.todolist.model.dto.RegistrationRequest;
import ua.spro.todolist.model.entity.User;
import ua.spro.todolist.repository.UserRepository;
import ua.spro.todolist.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User registerNewUser(RegistrationRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new RuntimeException("User already exists");
    }

    User user =
        User.builder()
            .username(request.username())
            .password(passwordEncoder.encode(request.password())) // Encode password
            .build();

    return userRepository.save(user);
  }
}
