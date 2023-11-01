package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GatewayExceptionHandler extends DefaultHandlerExceptionResolver {

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
}
