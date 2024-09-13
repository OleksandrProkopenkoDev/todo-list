package ua.spro.todolist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.spro.todolist.model.dto.RegistrationRequest;
import ua.spro.todolist.model.entity.User;
import ua.spro.todolist.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest request) {
    User registered = userService.registerNewUser(request);
    return ResponseEntity.ok("User registered successfully: %s".formatted(registered.getId()));
  }
}
