package ru.practicum.users.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.users.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(Integer[] ids, Integer from, Integer size);

    UserDto createUser(@Validated UserDto userDto);

    void deleteUser(Integer userId);
}
