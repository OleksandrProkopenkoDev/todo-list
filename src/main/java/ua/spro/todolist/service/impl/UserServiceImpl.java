package ua.spro.todolist.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.spro.todolist.model.dto.RegistrationRequest;
import ua.spro.todolist.model.dto.UserDto;
import ua.spro.todolist.model.entity.User;
import ua.spro.todolist.repository.UserRepository;
import ua.spro.todolist.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDto registerNewUser(RegistrationRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new RuntimeException("User already exists: %s".formatted(request.username()));
    }

    User user = mapToUser(request);
    user = userRepository.save(user);
    return mapToDto(user);
  }

  private UserDto mapToDto(User user) {
    return new UserDto(user.getId(), user.getUsername(), "*".repeat(user.getPassword().length()));
  }

  private User mapToUser(RegistrationRequest request) {
    return User.builder()
        .username(request.username())
        .password(passwordEncoder.encode(request.password()))
        .build();
  }
}
