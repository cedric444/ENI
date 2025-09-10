package com.stage.pizzeria.mapper;

import com.stage.pizzeria.dto.LoginDto;
import com.stage.pizzeria.dto.UserDto;
import com.stage.pizzeria.model.User;

public class UserMapper {

    public static UserDto mapToUserDto(User user) {

        return new UserDto(
                user.getId(),
                user.getLastName(),
                user.getFirstName(),
                user.getAddress(),
                user.getZipCode(),
                user.getCity(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getPassword(),
                user.getFidelityPoints(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public static User mapToUser(UserDto userDto) {

        return new User(
                userDto.getId(),
                userDto.getLastName(),
                userDto.getFirstName(),
                userDto.getAddress(),
                userDto.getZipCode(),
                userDto.getCity(),
                userDto.getEmail(),
                userDto.getPhoneNumber(),
                userDto.getPassword(),
                userDto.getFidelityPoints(),
                userDto.getCreatedAt(),
                userDto.getUpdatedAt()
        );
    }

    public static LoginDto mapToLoginDto(User user) {

        return new LoginDto(
                user.getEmail(),
                user.getPassword()
        );
    }

    public static User mapLoinToUser(LoginDto loginDto) {

        return new User(
                loginDto.getEmail(),
                loginDto.getPassword()
        );
    }
}
