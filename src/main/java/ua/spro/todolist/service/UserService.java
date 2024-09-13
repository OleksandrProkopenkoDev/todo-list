package ua.spro.todolist.service;

import ua.spro.todolist.model.dto.RegistrationRequest;
import ua.spro.todolist.model.dto.UserDto;

public interface UserService {

  UserDto registerNewUser(RegistrationRequest request);
}
