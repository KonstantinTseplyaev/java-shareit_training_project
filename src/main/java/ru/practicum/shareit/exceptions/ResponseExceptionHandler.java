package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.sql.SQLException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ResponseExceptionHandler extends DefaultHandlerExceptionResolver {

    @ExceptionHandler(value = ParamValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final ParamValidationException exp) {
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

    @ExceptionHandler(value = ItemNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final ItemNotFoundException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(404).body((Map.of("error", "Ошибка при поиске вещи", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = BookingNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final BookingNotFoundException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(404).body((Map.of("error", "Ошибка при поиске бронирования", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = RequestParamException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final RequestParamException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(404).body((Map.of("error", "Ошибка при указании параметров запроса", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = SQLException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final SQLException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(409).body((Map.of("error", "Ошибка при добавлении в базу данных", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = AvailableStatusException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final AvailableStatusException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(400).body((Map.of("error", "Ошибка статуса доступа к аренде", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = BookingDateException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final BookingDateException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(400).body((Map.of("error", "Ошибка при указании временных диапазонов бронирования", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = UnknownBookingStateException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final UnknownBookingStateException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(500).body((Map.of("error", "Unknown state: UNSUPPORTED_STATUS", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = RequestNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final RequestNotFoundException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(404).body((Map.of("error", "Ошибка при поиске запроса к вещи", "errorMessage",
                exp.getMessage())));
    }

    @ExceptionHandler(value = PaginationException.class)
    public ResponseEntity<Map<String, String>> handleValidationExpCount(final PaginationException exp) {
        log.error(exp.getMessage());
        return ResponseEntity.status(400).body((Map.of("error", "Ошибка пагинации", "errorMessage",
                exp.getMessage())));
    }
}
