package ru.practicum.users.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NoObjectException;
import ru.practicum.exception.ValidationException;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserMapper;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UsersRepository;

import java.util.List;

@AllArgsConstructor
@Component
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;

    @Override
    public List<UserDto> getUsers(Integer[] ids, Integer from, Integer size) {
        if (ids.length > 0) {
            return UserMapper.toUserDtoList(usersRepository.getByIdIn(ids));
        } else {
            return UserMapper.toUserDtoList(usersRepository.getAll(PageRequest.of(from / size, size)).getContent());
        }
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto == null) {
            throw new ValidationException("Объект пользователя пустой");
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(usersRepository.save(user));
    }

    @Override
    public void deleteUser(Integer userId) {
        if (usersRepository.existsById(userId)) {
            usersRepository.deleteById(userId);
        } else {
            throw new NoObjectException("User with id =" + userId + " was not found");
        }
    }
}
