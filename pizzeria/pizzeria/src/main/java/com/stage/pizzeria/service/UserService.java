package com.stage.pizzeria.service;

import com.stage.pizzeria.dto.LoginDto;
import com.stage.pizzeria.dto.LoginResponseDto;
import com.stage.pizzeria.dto.UserDto;
import com.stage.pizzeria.exception.EmailExistsException;
import com.stage.pizzeria.model.Role;
import com.stage.pizzeria.model.User;
import jakarta.validation.ConstraintViolationException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Collection;
import java.util.List;

public interface UserService {

    User addUser(UserDto userDto) throws EmailExistsException;
    User updateUser(UserDto userDto);
    void deleteUserById(long id);
    UserDto getUserById(long id);
    UserDto getUserByName(String lastName, String firstName);
    LoginDto getUserByEmail(String email);
    List<UserDto> getAllUsers();
    List<User> getUsersByRole(String roleName);
    boolean existsByEmail(String email);
    LoginResponseDto authenticateUser(LoginDto loginDto) throws BadCredentialsException, ConstraintViolationException;
}
