package ru.practicum.users.service;

import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserShortDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(Integer[] ids, Integer from, Integer size);

    UserDto createUser(UserDto userDto);

    void deleteUser(Integer userId);
}
