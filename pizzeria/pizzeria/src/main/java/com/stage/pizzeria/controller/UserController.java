package com.stage.pizzeria.controller;

import com.stage.pizzeria.dto.LoginDto;
import com.stage.pizzeria.dto.LoginResponseDto;
import com.stage.pizzeria.dto.UserDto;
import com.stage.pizzeria.exception.ControllerExceptionHandler;
import com.stage.pizzeria.exception.EmailExistsException;
import com.stage.pizzeria.service.UserService;
import com.stage.pizzeria.service.impl.JwtTokenProvider;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/admin/usersList")
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto) throws EmailExistsException, ConstraintViolationException {
        userService.addUser(userDto);
        return new ResponseEntity<>("Utilisateur enregistré avec succès", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) throws ConstraintViolationException {
        try {
            LoginResponseDto response = userService.authenticateUser(loginDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}
