package ru.practicum.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    Integer id;
    @NotBlank
    @Size(min = 2, max = 250)
    String name;
    @Email
    @NotBlank
    @Size(min = 6, max = 254)
    String email;
}
