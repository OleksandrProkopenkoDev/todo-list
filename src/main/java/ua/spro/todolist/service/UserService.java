package ua.spro.todolist.service;

import ua.spro.todolist.model.dto.RegistrationRequest;
import ua.spro.todolist.model.entity.User;

public interface UserService {

  User registerNewUser(RegistrationRequest request);
}
