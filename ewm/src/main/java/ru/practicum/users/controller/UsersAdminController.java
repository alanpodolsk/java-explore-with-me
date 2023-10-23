package ru.practicum.users.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@AllArgsConstructor
public class UsersAdminController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers (@RequestParam (defaultValue = "") Integer[] ids, @RequestParam (defaultValue = "0") Integer from,
                                   @RequestParam (defaultValue = "10") Integer size){
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto){
        return userService.createUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(Integer userId){
        userService.deleteUser(userId);
    }
}
