package ru.practicum.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    HttpStatus status;
    String reason;
    String message;
    String timestamp;
    List<String> errors;
}
