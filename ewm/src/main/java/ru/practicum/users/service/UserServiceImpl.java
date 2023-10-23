package ru.practicum.users.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.repository.UsersRepository;

import java.util.List;
@AllArgsConstructor
@Component
public class UserServiceImpl implements UserService{

    private final UsersRepository usersRepository;
    @Override
    public List<UserDto> getUsers(Integer[] ids, Integer from, Integer size) {
        return null;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return null;
    }

    @Override
    public void deleteUser(Integer userId) {

    }
}
