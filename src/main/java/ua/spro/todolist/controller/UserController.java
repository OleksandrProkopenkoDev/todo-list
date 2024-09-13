package ua.spro.todolist.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.spro.todolist.model.dto.RegistrationRequest;
import ua.spro.todolist.model.dto.UserDto;
import ua.spro.todolist.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public UserDto registerUser(@RequestBody RegistrationRequest request) {
    return userService.registerNewUser(request);
  }
}
