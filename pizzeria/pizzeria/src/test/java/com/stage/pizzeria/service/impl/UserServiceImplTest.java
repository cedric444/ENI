package com.stage.pizzeria.service.impl;

import com.stage.pizzeria.dto.LoginDto;
import com.stage.pizzeria.dto.LoginResponseDto;
import com.stage.pizzeria.dto.UserDto;
import com.stage.pizzeria.exception.EmailExistsException;
import com.stage.pizzeria.model.Role;
import com.stage.pizzeria.model.User;
import com.stage.pizzeria.repository.RoleRepository;
import com.stage.pizzeria.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private Validator validator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserServiceImpl userService;


    private User existingUser;
    private UserDto validUserDto;
    private UserDto invalidUserDto;
    private Role role;
    private static final long EXISTING_USER_ID = 1L;
    private static final long NON_EXISTING_USER_ID = 999L;
    private static final String USER_EMAIL = "toto@email.com";
    private static final String FIRST_NAME = "Tata";
    private static final String LAST_NAME = "Toto";

    @BeforeEach
    void setup() {
        userService = new UserServiceImpl(userRepository, roleRepository, passwordEncoder, authenticationManager, jwtTokenProvider, validator);

        // Role
        role = new Role();
        role.setId(2);
        role.setName("USER");

        // Existing user
        existingUser = new User();
        existingUser.setId(EXISTING_USER_ID);
        existingUser.setLastName(LAST_NAME);
        existingUser.setFirstName(FIRST_NAME);
        existingUser.setAddress("1, rue du test");
        existingUser.setZipCode("17520");
        existingUser.setCity("TestCity");
        existingUser.setEmail(USER_EMAIL);
        existingUser.setPhoneNumber("0601020304");
        existingUser.setPassword("password");
        existingUser.setFidelityPoints(0);
        existingUser.setRoles(Collections.singletonList(role));

        // Valid DTO
        validUserDto = new UserDto();
        validUserDto.setId(EXISTING_USER_ID);
        validUserDto.setLastName(LAST_NAME);
        validUserDto.setFirstName(FIRST_NAME);
        validUserDto.setAddress("1, rue du test");
        validUserDto.setZipCode("17520");
        validUserDto.setCity("TestCity");
        validUserDto.setEmail(USER_EMAIL);
        validUserDto.setPhoneNumber("0601020304");
        validUserDto.setPassword("password");
        validUserDto.setFidelityPoints(0);

        //Invalid DTO
        invalidUserDto = new UserDto();
        invalidUserDto.setLastName("");
        invalidUserDto.setFirstName("");
        invalidUserDto.setEmail("email-invalid");
    }



    @Nested
    @DisplayName("AddUser Tests")
    class AddUserTest {

        @Test
        void addUser_WithValidUserDto_ShouldReturnSavedUser() throws EmailExistsException {
            when(validator.validate(validUserDto)).thenReturn(Collections.emptySet());
            when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
            when(userRepository.save(any(User.class))).thenReturn(existingUser);

            User result = userService.addUser(validUserDto);

            assertNotNull(result);
            assertEquals(existingUser.getId(), result.getId());
            assertEquals(existingUser.getLastName(), result.getLastName());
            assertEquals(existingUser.getFirstName(), result.getFirstName());

            verify(validator, times(1)).validate(validUserDto);
            verify(roleRepository, times(1)).findByName("USER");
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void addUser_WithInvalidUserDto_ShouldThrowConstraintViolationException() {
            Set<ConstraintViolation<UserDto>> violations = new HashSet<>();
            ConstraintViolation<UserDto> violation = mock(ConstraintViolation.class);
            violations.add(violation);
            when(validator.validate(invalidUserDto)).thenReturn(violations);

            assertThrows(ConstraintViolationException.class, () -> {
                userService.addUser(invalidUserDto);
            });

            verify(validator, times(1)).validate(invalidUserDto);
            verify(roleRepository, never()).findByName("USER");
            verify(userRepository, never()).save(any());
        }

        @Test
        void addUser_WhenRoleNotFound_ShouldThrowException() {
            when(validator.validate(validUserDto)).thenReturn(Collections.emptySet());
            when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                userService.addUser(validUserDto);
            });

            verify(validator, times(1)).validate(validUserDto);
            verify(roleRepository, times(1)).findByName("USER");
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("UpdateUser tests")
    class UpdateUserTests {

        @Test
        void updateUser_WithExistingUser_ShouldReturnUpdatedUser() {
            UserDto updateDto = new UserDto();
            updateDto.setId(EXISTING_USER_ID);
            updateDto.setLastName("Doe");
            updateDto.setFirstName("John");
            updateDto.setAddress("3 rue des tests");
            updateDto.setZipCode("59000");
            updateDto.setCity("Lille");
            updateDto.setEmail("johndoe@email.com");
            updateDto.setPhoneNumber("0102030405");
            updateDto.setPassword("newPassword");
            updateDto.setFidelityPoints(1);

            when(userRepository.findById(EXISTING_USER_ID)).thenReturn(Optional.of(existingUser));
            when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
            when(userRepository.save(any(User.class))).thenReturn(existingUser);

            User result = userService.updateUser(updateDto);

            assertNotNull(result);
            verify(userRepository, times(1)).findById(EXISTING_USER_ID);
            verify(roleRepository, times(1)).findByName("USER");
            verify(userRepository, times(1)).save(existingUser);

            assertEquals("Doe", result.getLastName());
            assertEquals("John", result.getFirstName());
            assertEquals("johndoe@email.com", result.getEmail());
        }

        @Test
        void updateUser_WithNonExistingUser_ShouldThrowException() {
            validUserDto.setId(NON_EXISTING_USER_ID);

            when(userRepository.findById(NON_EXISTING_USER_ID)).thenReturn(Optional.empty());
            when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));

            assertThrows(RuntimeException.class, () -> {
                userService.updateUser(validUserDto);
            });

            verify(userRepository, times(1)).findById(NON_EXISTING_USER_ID);
            verify(roleRepository, times(1)).findByName(any());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("DeleteUserById Tests")
    class DeleteUserByIdTests {

        @Test
        void deleteUserById_WhenUserExists_ShouldDeleteUser() {

            when(userRepository.findById(EXISTING_USER_ID)).thenReturn(Optional.of(existingUser));

            assertDoesNotThrow(() -> userService.deleteUserById(EXISTING_USER_ID));

            verify(userRepository, times(1)).findById(EXISTING_USER_ID);
            verify(userRepository, times(1)).deleteById(EXISTING_USER_ID);
        }

        @Test
        void deleteUserById_WithNonExistingUser_ShouldThrowNullPointerException() {

            when(userRepository.findById(NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                userService.getUserById(NON_EXISTING_USER_ID);
            });

            assertEquals("L'utilisateur n'existe pas", exception.getMessage());

            verify(userRepository, times(1)).findById(NON_EXISTING_USER_ID);
            verify(userRepository, never()).deleteById(NON_EXISTING_USER_ID);
        }
    }

    @Nested
    @DisplayName("GetUserById Tests")
    class GetUserByIdTests {

        @Test
        void getUserById_WithExistingUser_ShouldReturnUserDto() {
            when(userRepository.findById(EXISTING_USER_ID)).thenReturn(Optional.of(existingUser));

            UserDto result = userService.getUserById(EXISTING_USER_ID);

            assertNotNull(result);
            assertEquals(EXISTING_USER_ID, result.getId());
            assertEquals(LAST_NAME, result.getLastName());
            assertEquals(FIRST_NAME, result.getFirstName());
            assertEquals(USER_EMAIL, result.getEmail());

            verify(userRepository, times(1)).findById(EXISTING_USER_ID);
        }

        @Test
        void getUserById_WithNonExistingUser_ShouldThrowNullPointerException() {
            when(userRepository.findById(NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

            NullPointerException exception = assertThrows(NullPointerException.class, () -> {
                userService.getUserById(NON_EXISTING_USER_ID);
            });

            assertEquals("L'utilisateur n'existe pas", exception.getMessage());
            verify(userRepository, times(1)).findById(NON_EXISTING_USER_ID);
        }
    }

    @Nested
    @DisplayName("getUserByName Tests")
    class GetUserByNameTests {

        @Test
        void getUserByName_WithExistingUser_ShouldReturnUserDto() {
            when(userRepository.findByLastNameAndFirstName(LAST_NAME, FIRST_NAME))
                    .thenReturn(Optional.of(existingUser));

        UserDto result = userService.getUserByName(LAST_NAME, FIRST_NAME);

        assertNotNull(result);
        assertEquals(EXISTING_USER_ID, result.getId());
        assertEquals(LAST_NAME, result.getLastName());
        assertEquals(FIRST_NAME, result.getFirstName());

        verify(userRepository, times(1)).findByLastNameAndFirstName(LAST_NAME, FIRST_NAME);
        }

        @Test
        void getUserByName_WithNonExistingUser_ShouldThrowException() {
            String nonExistingLastName = "unknown";
            String nonExistingFirstName = "Who";

            when(userRepository.findByLastNameAndFirstName(nonExistingLastName, nonExistingFirstName)).
                    thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                userService.getUserByName(nonExistingLastName, nonExistingFirstName);
            });

            verify(userRepository, times(1)).findByLastNameAndFirstName(nonExistingLastName, nonExistingFirstName);
        }
    }

    @Nested
    @DisplayName("getUserByEmail Tests")
    class GetUserByEmailTests {

        @Test
        void getUserByEmail_WithExistingUser_ShouldReturnUserDto() {
            when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(existingUser));

            LoginDto result = userService.getUserByEmail(USER_EMAIL);

            assertNotNull(result);;
            assertEquals(USER_EMAIL, result.getEmail());
            assertEquals(existingUser.getPassword(), result.getPassword());

            verify(userRepository, times(1)).findByEmail(USER_EMAIL);
        }

        @Test
        void getUserByEmail_WithNonExistingUser_ShouldThrowException() {
            String nonExistingEmail = "unknown@email.com";
            when(userRepository.findByEmail(nonExistingEmail)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                userService.getUserByEmail(nonExistingEmail);
            });

            verify(userRepository, times(1)).findByEmail(nonExistingEmail);
        }
    }

    @Nested
    @DisplayName("getAllUsers Tests")
    class GetAllUsersTests {

        @Test
        void getAllUsers_ShouldReturnListOfUserDto() {
            User user2 = new User();
            user2.setId(2L);
            user2.setLastName("Noone");
            user2.setFirstName("Jane");
            user2.setEmail("jane.noone@email.com");

            List<User> users = Arrays.asList(existingUser, user2);
            when(userRepository.findAll()).thenReturn(users);

            List<UserDto> result = userService.getAllUsers();

            assertNotNull(result);
            assertEquals(2, result.size());

            UserDto firstUserDto = result.get(0);
            assertEquals(EXISTING_USER_ID, firstUserDto.getId());
            assertEquals(LAST_NAME, firstUserDto.getLastName());

            UserDto secondUserDto = result.get(1);
            assertEquals(2L, secondUserDto.getId());
            assertEquals("Noone", secondUserDto.getLastName());

            verify(userRepository, times(1)).findAll();
        }

        @Test
        void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() {
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            List<UserDto> result = userService.getAllUsers();

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(userRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("getUsersByRole tests")
    class GetUsersByRoleTests {

        @Test
        void getUsersByRole_WithExistingRole_ShouldReturnListOfUsers() {
            User user2 = new User();
            user2.setId(2L);
            user2.setLastName("Doe");
            user2.setFirstName("John");
            user2.setEmail("johndoe@email.com");
            user2.setRoles(Collections.singletonList(role));

            List<User> users = Arrays.asList(existingUser, user2);
            when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
            when(userRepository.findAllByRole(role.getId())).thenReturn(users);

            List<User> result = userService.getUsersByRole(role.getName());

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(existingUser.getId(), result.get(0).getId());
            assertEquals(user2.getId(), result.get(1).getId());

            verify(userRepository, times(1)).findAllByRole(role.getId());

        }

        @Test
        void getUsersByRole_WithNoUsersForRole_ShouldReturnEmptyList() {
            Role adminRole = new Role();
            adminRole.setId(1);
            adminRole.setName("ADMIN");

            when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
            when(userRepository.findAllByRole(adminRole.getId())).thenReturn(Collections.emptyList());

            List<User> result = userService.getUsersByRole(adminRole.getName());

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(userRepository, times(1)).findAllByRole(adminRole.getId());
        }
    }

    @Nested
    @DisplayName("authenticationUser tests")
    class AuthenticationUserTests {

        private LoginDto validLoginDto;
        private LoginDto invalidLoginDto;

        @BeforeEach
        void setupLogin() {
            validLoginDto = new LoginDto();
            validLoginDto.setEmail("toto@example.com");
            validLoginDto.setPassword("password");

            invalidLoginDto = new LoginDto();
            invalidLoginDto.setEmail("invalid-email");
            invalidLoginDto.setPassword("");
        }

        @Test
        void authenticateUser_WithValidCredentials_ShouldReturnLoinResponseDto() {
            String expectedToken = "eyJhbGciOiJIUzUxMiJ9.test.token";
            Authentication mockAuthentication = mock(Authentication.class);

            when(validator.validate(validLoginDto)).thenReturn(Collections.emptySet());
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(jwtTokenProvider.generateToken(mockAuthentication)).thenReturn(expectedToken);

            LoginResponseDto result = userService.authenticateUser(validLoginDto);

            assertNotNull(result);
            assertEquals(expectedToken, result.getToken());
            assertEquals("Bearer", result.getType());

            verify(validator, times(1)).validate(validLoginDto);
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenProvider, times(1)).generateToken(mockAuthentication);
        }

        @Test
        void authenticateUser_WithInvalidCredentials_ShouldReturnBadCredentialsException() {

            when(validator.validate(validLoginDto)).thenReturn(Collections.emptySet());
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
                userService.authenticateUser(validLoginDto);
            });

            assertEquals("Invalid credentials", exception.getMessage());

            verify(validator, times(1)).validate(validLoginDto);
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenProvider, never()).generateToken(any(Authentication.class));
        }

        @Test
        void authenticateUser_WithValidationErrors_ShouldThrowConstraintValidationException() {

            Set<ConstraintViolation<LoginDto>> violations = new HashSet<>();
            ConstraintViolation<LoginDto> violation = mock(ConstraintViolation.class);
            violations.add(violation);

            when(validator.validate(invalidLoginDto)).thenReturn(violations);

            ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
                userService.authenticateUser(invalidLoginDto);
            });

            assertNotNull(exception);
            assertFalse(exception.getConstraintViolations().isEmpty());

            verify(validator, times(1)).validate(invalidLoginDto);
            verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenProvider, never()).generateToken(any(Authentication.class));
        }

        @Test
        void authenticateUser_WithNullEmail_ShouldThrowConstraintViolationException() {
            LoginDto nullEmailDto = new LoginDto();
            nullEmailDto.setEmail(null);
            nullEmailDto.setPassword("password");

            Set<ConstraintViolation<LoginDto>> violations = new HashSet<>();
            ConstraintViolation<LoginDto> violation = mock(ConstraintViolation.class);
            violations.add(violation);

            when(validator.validate(nullEmailDto)).thenReturn(violations);

            assertThrows(ConstraintViolationException.class, () -> {
                userService.authenticateUser(nullEmailDto);
            });

            verify(validator, times(1)).validate(nullEmailDto);
            verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenProvider, never()).generateToken(any(Authentication.class));
        }

        @Test
        void authenticateUser_WithEmptyEmail_ShouldThrowConstraintViolationException() {

            LoginDto emptyEmailDto = new LoginDto();
            emptyEmailDto.setEmail("");
            emptyEmailDto.setPassword("password");

            Set<ConstraintViolation<LoginDto>> violations = new HashSet<>();
            ConstraintViolation<LoginDto> violation = mock(ConstraintViolation.class);
            violations.add(violation);

            when(validator.validate(emptyEmailDto)).thenReturn(violations);

            assertThrows(ConstraintViolationException.class, () -> {
                userService.authenticateUser(emptyEmailDto);
            });

            verify(validator, times(1)).validate(emptyEmailDto);
            verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenProvider, never()).generateToken(any(Authentication.class));
        }

        @Test
        void authenticateUser_WithNullPassword_ShouldThrowConstraintViolationException() {
            LoginDto nullPasswordDto = new LoginDto();
            nullPasswordDto.setEmail("toto@example.com");
            nullPasswordDto.setPassword(null);

            Set<ConstraintViolation<LoginDto>> violations = new HashSet<>();
            ConstraintViolation<LoginDto> violation = mock(ConstraintViolation.class);
            violations.add(violation);

            when(validator.validate(nullPasswordDto)).thenReturn(violations);

            assertThrows(ConstraintViolationException.class, () -> {
                userService.authenticateUser(nullPasswordDto);
            });

            verify(validator, times(1)).validate(nullPasswordDto);
            verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenProvider, never()).generateToken(any(Authentication.class));
        }

        @Test
        void authenticateUser_WithEmptyPassword_ShouldThrowConstraintViolationException() {

            LoginDto emptyPasswordDto = new LoginDto();
            emptyPasswordDto.setEmail("toto@example.com");
            emptyPasswordDto.setPassword("");

            Set<ConstraintViolation<LoginDto>> violations = new HashSet<>();
            ConstraintViolation<LoginDto> violation = mock(ConstraintViolation.class);
            violations.add(violation);

            when(validator.validate(emptyPasswordDto)).thenReturn(violations);

            assertThrows(ConstraintViolationException.class, () -> {
                userService.authenticateUser(emptyPasswordDto);
            });

            verify(validator, times(1)).validate(emptyPasswordDto);
            verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenProvider, never()).generateToken(any(Authentication.class));
        }

        @Test
        void authenticateUser_WithInvalidEmailFormat_ShouldThrowConstraintViolationException() {

            LoginDto invalidEmailFormatDto = new LoginDto();
            invalidEmailFormatDto.setEmail("invalid-email-format");
            invalidEmailFormatDto.setPassword("password");

            Set<ConstraintViolation<LoginDto>> violations = new HashSet<>();
            ConstraintViolation<LoginDto> violation = mock(ConstraintViolation.class);
            violations.add(violation);

            when(validator.validate(invalidEmailFormatDto)).thenReturn(violations);

            assertThrows(ConstraintViolationException.class, () -> {
                userService.authenticateUser(invalidEmailFormatDto);
            });

            verify(validator, times(1)).validate(invalidEmailFormatDto);
            verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenProvider, never()).generateToken(any(Authentication.class));
        }

        @Test
        void authenticateUser_WhenAuthenticationManagerThrowsException_ShouldPropagateException() {

            when(validator.validate(validLoginDto)).thenReturn(Collections.emptySet());
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new RuntimeException("Authentication service unavailable"));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                userService.authenticateUser(validLoginDto);
            });

            assertEquals("Authentication service unavailable", exception.getMessage());

            verify(validator, times(1)).validate(validLoginDto);
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenProvider, never()).generateToken(any(Authentication.class));
        }

        @Test
        void authenticateUser_ShouldSetSecurityContext() {
            String expectedToken = "eyJhbGciOiJIUzUxMiJ9.test.token";
            Authentication mockAuthentication = mock(Authentication.class);

            when(validator.validate(validLoginDto)).thenReturn(Collections.emptySet());
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn(expectedToken);

            LoginResponseDto result = userService.authenticateUser(validLoginDto);

            assertNotNull(result);
            assertEquals(mockAuthentication, SecurityContextHolder.getContext().getAuthentication());

            verify(validator, times(1)).validate(validLoginDto);
            verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenProvider, times(1)).generateToken(mockAuthentication);
        }
    }

}