package com.stage.pizzeria.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stage.pizzeria.dto.LoginDto;
import com.stage.pizzeria.dto.LoginResponseDto;
import com.stage.pizzeria.dto.UserDto;
import com.stage.pizzeria.exception.EmailExistsException;
import com.stage.pizzeria.model.User;
import com.stage.pizzeria.service.UserService;
import com.stage.pizzeria.service.impl.JwtTokenProvider;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.rmi.UnexpectedException;
import java.util.*;

import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto validUserDto;
    private List<UserDto> userDtoList;
    private LoginDto validLoginDto;

    @BeforeEach
    void setup() {
        //Initialize a valid UserDto for the tests
        validUserDto = new UserDto();
        validUserDto.setId(1L);
        validUserDto.setLastName("toto");
        validUserDto.setFirstName("tata");
        validUserDto.setAddress("1 , rue du test");
        validUserDto.setZipCode("75000");
        validUserDto.setCity("TestCity");
        validUserDto.setEmail("toto@example.com");
        validUserDto.setPhoneNumber("0123456789");
        validUserDto.setPassword("password123");
        validUserDto.setFidelityPoints(0);
        validUserDto.setCreatedAt(new Date());
        validUserDto.setCreatedAt(new Date());

        // Initialize a list of Users for the tests
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setLastName("titi");
        user2.setFirstName("tutu");
        user2.setAddress("2, rue du test");
        user2.setZipCode("59000");
        user2.setCity("CityTest");
        user2.setEmail("titi@example.com");
        user2.setPhoneNumber("0612345789");
        user2.setPassword("password456");
        user2.setFidelityPoints(0);
        user2.setCreatedAt(new Date());
        user2.setUpdatedAt(new Date());

        userDtoList = Arrays.asList(validUserDto, user2);

        validLoginDto = new LoginDto();
        validLoginDto.setEmail("toto@example.com");
        validLoginDto.setPassword("password123");
    }

    @Test
    @WithMockUser(roles = "USER")
     void getUsers_ShouldReturnListOfUsers() throws Exception {

        when(userService.getAllUsers()).thenReturn(userDtoList);

        mockMvc.perform(get("/api/users/admin/usersList")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("toto@example.com")))
                .andExpect(jsonPath("$[0].lastName", is("toto")))
                .andExpect(jsonPath("$[0].firstName", is("tata")))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email", is("titi@example.com")))
                .andExpect(jsonPath("$[1].lastName", is("titi")))
                .andExpect(jsonPath("$[1].firstName", is("tutu")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUsers_WhenNoUsers_ShouldReturnEmptyList() throws Exception {

        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/admin/usersList")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUsers_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {

        when(userService.getAllUsers()).thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/users/admin/usersList")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    void register_WhenUserRegistrationSucceeds_ShouldReturnSuccessMessage() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setEmail("toto@example.com");
        userDto.setLastName("toto");
        userDto.setFirstName("tata");
        userDto.setPassword("password123");

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("toto@example.com");

        when(userService.addUser(any(UserDto.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Utilisateur enregistré avec succès"));

        verify(userService, times(1)).addUser(any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void register_WhenEmailAlreadyExists_ShouldReturnBadRequest() throws Exception {
        String errorMessage = "L'email toto@example.com est déjà utilisé sur un compte";

        when(userService.addUser(any(UserDto.class)))
                .thenThrow(new EmailExistsException(errorMessage));

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));

        verify(userService, times(1)).addUser(any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void register_WithConstraintViolation_ShouldReturnBadRequest() throws Exception {
        Set<ConstraintViolation<UserDto>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        when(userService.addUser(any(UserDto.class))).thenThrow(exception);

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Validation failed"));

        verify(userService, times(1)).addUser(any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void register_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void register_WithEmptyRequestBody_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verify(userService, never()).addUser(any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void register_WithMissingContentType_ShouldReturnUnsupportedMediaType() throws Exception {

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isUnsupportedMediaType());

        verify(userService, never()).addUser(any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void register_WhenServiceThrowsUnexpectedException_ShouldReturnInternalServerError() throws Exception {

        when(userService.addUser(any(UserDto.class))).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUserDto)))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).addUser(any(UserDto.class));

    }

    @Test
    @WithMockUser(roles = "USER")
    void register_WithSpecialCharactersInData_ShouldWork() throws Exception {

        UserDto userWithSpecialChars = new UserDto();
        userWithSpecialChars.setLastName("O'Connor");
        userWithSpecialChars.setFirstName("José--maria");
        userWithSpecialChars.setAddress("1 rue de l'église");
        userWithSpecialChars.setZipCode("59000");
        userWithSpecialChars.setCity("Saint-Etienne");
        userWithSpecialChars.setEmail("jose.maria@example.com");
        userWithSpecialChars.setPhoneNumber("0123456789");
        userWithSpecialChars.setPassword("password123");

        User mockUser= new User();
        mockUser.setId(1L);

        when(userService.addUser(any(UserDto.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithSpecialChars)))
                .andExpect(status().isOk())
                .andExpect(content().string("Utilisateur enregistré avec succès"));

        verify(userService, times(1)).addUser(any(UserDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void login_WithValidCredentials_ShouldReturnJwtToken() throws Exception {
        String expectedToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0b3RvQGV4YW1wbGUuY29tIiwiaWF0IjoxNjk5ODg4ODAwLCJleHAiOjE2OTk5NzUyMDB9.test";

        LoginResponseDto expectedResponse = new LoginResponseDto();
        expectedResponse.setToken(expectedToken);
        expectedResponse.setType("Bearer");

        when(userService.authenticateUser(any(LoginDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(expectedToken))
                .andExpect(jsonPath("$.type").value("Bearer"));

        verify(userService, times(1)).authenticateUser(any(LoginDto.class));

    }

    @Test
    @WithMockUser(roles = "USER")
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        when(userService.authenticateUser(any(LoginDto.class)))
                .thenThrow(new BadCredentialsException("invalid credentials"));

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginDto)))
                .andExpect(status().isUnauthorized());

        verify(userService, times(1)).authenticateUser(any(LoginDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void login_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        LoginDto invalidLoginDto = new LoginDto();
        invalidLoginDto.setEmail("invalid-email");
        invalidLoginDto.setPassword("password123");

        Set<ConstraintViolation<LoginDto>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("Email invalide", violations);

        when(userService.authenticateUser(any(LoginDto.class))).thenThrow(exception);

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoginDto)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).authenticateUser(any(LoginDto.class));

    }

    @Test
    @WithMockUser(roles ="USER")
    void login_WithEmptyEmail_ShouldReturnBadRequest() throws Exception {
        LoginDto emptyEmailDto = new LoginDto();
        emptyEmailDto.setEmail("");
        emptyEmailDto.setPassword("password123");

        Set<ConstraintViolation<LoginDto>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("Email requis", violations);

        when(userService.authenticateUser(any(LoginDto.class))).thenThrow(exception);

        mockMvc.perform(post("/api/users/login")
               .with(csrf())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(emptyEmailDto)))
               .andExpect(status().isBadRequest());

        verify(userService, times(1)).authenticateUser(any(LoginDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void login_WithEmptyPassword_ShouldReturnBadRequest() throws Exception {
        LoginDto emptyPasswordDto = new LoginDto();
        emptyPasswordDto.setEmail("toto@example.com");
        emptyPasswordDto.setPassword("");

        Set<ConstraintViolation<LoginDto>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException("Mot de passe requis", violations);

        when(userService.authenticateUser(any(LoginDto.class))).thenThrow(exception);

        mockMvc.perform(post("/api/users/login")
               .with(csrf())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(emptyPasswordDto)))
               .andExpect(status().isBadRequest());

        verify(userService, times(1)).authenticateUser(any(LoginDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void login_WithNullCredentialsShouldReturnUnauthorized() throws Exception {
        LoginDto nullCredentialsDto = new LoginDto();
        nullCredentialsDto.setEmail(null);
        nullCredentialsDto.setPassword(null);

        when(userService.authenticateUser(any(LoginDto.class))).thenThrow(new BadCredentialsException("invalid credentials"));

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nullCredentialsDto)))
                .andExpect(status().isUnauthorized());

        verify(userService, times(1)).authenticateUser(any(LoginDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void login_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(userService, never()).authenticateUser(any(LoginDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void login_WithEmptyRequestBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verify(userService, never()).authenticateUser(any(LoginDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void login_WithMissingContentType_ShouldReturnUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .content(objectMapper.writeValueAsString(validLoginDto)))
                .andExpect(status().isUnsupportedMediaType());

        verify(userService, never()).authenticateUser(any(LoginDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void login_WhenServiceThrowsUnexpectedException_ShouldReturnInternalServerError() throws Exception {
        when(userService.authenticateUser(any(LoginDto.class)))
                .thenThrow(new RuntimeException("service indisponible"));

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginDto)))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).authenticateUser(any(LoginDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void login_WithSpecialCharactersInPassword_ShouldWork() throws Exception {
        LoginDto specialCharsDto = new LoginDto();
        specialCharsDto.setEmail("toto@example.com");
        specialCharsDto.setPassword("p@ssW0rd!#$%");

        String expectedToken = "eyJhbGciOiJIUzUxMiJ9.special.chars.token";
        LoginResponseDto expectedResponse = new LoginResponseDto();
        expectedResponse.setToken(expectedToken);
        expectedResponse.setType("Bearer");

        when(userService.authenticateUser(any(LoginDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialCharsDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(expectedToken))
                .andExpect(jsonPath("$.type").value("Bearer"));

        verify(userService, times(1)).authenticateUser(any(LoginDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void login_WithLongPassword_ShouldWork() throws Exception {
        LoginDto longPasswordDto = new LoginDto();
        longPasswordDto.setEmail("toto@example.com");
        longPasswordDto.setPassword("thisIsAVeryLongPasswordWithMoreThan50CharactersToTestTheLimit");

        String expectedToken = "eyJhbGciOiJIUzUxMiJ9.special.chars.token";
        LoginResponseDto expectedResponse = new LoginResponseDto();
        expectedResponse.setToken(expectedToken);
        expectedResponse.setType("Bearer");

        when(userService.authenticateUser(any(LoginDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/api/users/login")
               .with(csrf())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(longPasswordDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").value(expectedToken));

        verify(userService, times(1)).authenticateUser(any(LoginDto.class));
    }
}