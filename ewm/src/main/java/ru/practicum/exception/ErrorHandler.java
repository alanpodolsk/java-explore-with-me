package ru.practicum.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.dto.ApiError;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        return new ApiError(HttpStatus.CONFLICT, "Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER), null);

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoObjectException(final NoObjectException e) {
        return new ApiError(HttpStatus.NOT_FOUND, "The required object was not found.", e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER), null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER), null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, "Incorrectly made request.", e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER), null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "IllegalArgumentException", e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER), null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        return new ApiError(HttpStatus.BAD_REQUEST, "Validation error", e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER), null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return new ApiError(HttpStatus.CONFLICT, "Conflict error", e.getMessage(), LocalDateTime.now().format(DATE_TIME_FORMATTER), null);
    }


}
