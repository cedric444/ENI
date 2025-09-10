package com.stage.pizzeria.service.impl;

import com.stage.pizzeria.dto.LoginDto;
import com.stage.pizzeria.dto.LoginResponseDto;
import com.stage.pizzeria.dto.UserDto;
import com.stage.pizzeria.exception.EmailExistsException;
import com.stage.pizzeria.mapper.UserMapper;
import com.stage.pizzeria.model.Role;
import com.stage.pizzeria.model.User;
import com.stage.pizzeria.repository.RoleRepository;
import com.stage.pizzeria.repository.UserRepository;
import com.stage.pizzeria.service.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private final Validator validator;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, Validator validator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.validator = validator;
    }

    @Override
    public User addUser(UserDto userDto) throws EmailExistsException {
        if(userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailExistsException("L'email " + userDto.getEmail() + " est déjà utilisé sur un compte");
        };
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        User user = UserMapper.mapToUser(userDto);
        Role role = roleRepository.findByName("USER").orElseThrow();
        Collection<Role> roles = new ArrayList<>();
        roles.add(role);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userRepository.save(user);

    }

    @Override
    public User updateUser(UserDto userDto) {
        Collection<Role> roles = new ArrayList<>();
        Role role =roleRepository.findByName("USER").orElseThrow();
        roles.add(role);
        User existingUser = userRepository.findById(userDto.getId()).orElseThrow();
        existingUser.setLastName(userDto.getLastName());
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setAddress(userDto.getAddress());
        existingUser.setZipCode(userDto.getZipCode());
        existingUser.setCity(userDto.getCity());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPhoneNumber(userDto.getPhoneNumber());
        existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        existingUser.setFidelityPoints(userDto.getFidelityPoints());
        existingUser.setRoles(roles);
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUserById(long id) {
        User ExistingUser = userRepository.findById(id).orElseThrow(() -> new NullPointerException("L'utilisateur n'existe pas"));
        userRepository.deleteById(id);
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NullPointerException("L'utilisateur n'existe pas"));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto getUserByName(String lastName, String firstName) {
        User user = userRepository.findByLastNameAndFirstName(lastName, firstName).orElseThrow();
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public LoginDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return UserMapper.mapToLoginDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();
        for(User user : users) {
              userDtos.add(UserMapper.mapToUserDto(user));
        }
        return userDtos;
    }

    @Override
    public List<User> getUsersByRole(String roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow();
        int idRole = role.getId();
        return userRepository.findAllByRole(idRole);

    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Authenticate a User and generate a token JWT
     * @param loginDto User's credentials
     * @return LoginResponseDto contains the token
     * @throws BadCredentialsException if credentials are invalid
     * @throws ConstraintViolationException if the validation fails
     */
    @Override
    public LoginResponseDto authenticateUser(LoginDto loginDto) throws BadCredentialsException {
        Set<ConstraintViolation<LoginDto>> violations = validator.validate(loginDto);
        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        return new LoginResponseDto(token);
    }

}
