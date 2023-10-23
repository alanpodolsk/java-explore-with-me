package ru.practicum.users.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    Integer id;
    String name;
    String email;
}
