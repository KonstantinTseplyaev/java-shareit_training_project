package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ResponseExceptionHandler extends DefaultHandlerExceptionResolver {

    @ExceptionHandler(value = ValidationUserException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final ValidationUserException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(409).body((Map.of("error", "Ошибка при валидации", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final UserNotFoundException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(404).body((Map.of("error", "Ошибка при поиске пользователя", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = ItemStatusException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final ItemStatusException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(400).body((Map.of("error", "Ошибка статуса доступности вещи", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = RequestParamException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final RequestParamException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(404).body((Map.of("error", "Ошибка при указании параметров запроса", "errorMessage",
                exp.getMessage())));
    }
}
