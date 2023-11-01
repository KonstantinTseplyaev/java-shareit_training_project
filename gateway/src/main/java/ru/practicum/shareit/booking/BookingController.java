package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingDateException;
import ru.practicum.shareit.exception.UnknownBookingStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


import java.time.LocalDateTime;

import static ru.practicum.shareit.client.BaseClient.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient client;

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                                         @RequestParam(defaultValue = "ALL") String state,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                         @RequestParam(defaultValue = "20") @Positive int size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new UnknownBookingStateException("Unknown state: " + state));
        log.info("Get-запрос: запрос на получение всех бронирований со статусом {} пользователя {}.", state, userId);
        return client.getBookingsByUserId(userId, bookingState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwnerId(@RequestHeader(USER_ID_HEADER) long ownerId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                          @RequestParam(defaultValue = "20") @Positive int size) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new UnknownBookingStateException("Unknown state: " + state));
        log.info("Get-запрос: запрос на получение всех бронирований со статусом {} владельца {}.", state, ownerId);
        return client.getBookingsByOwnerId(ownerId, bookingState, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                                @RequestBody @Valid BookingCreationDto bookingDto) {
        checkValidBooking(bookingDto);
        log.info("Post-запрос: запрос на бронирование {} пользователем {}.", bookingDto, userId);
        return client.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmationBooking(@RequestHeader(USER_ID_HEADER) long ownerId,
                                                      @PathVariable long bookingId,
                                                      @RequestParam boolean approved) {
        log.info("Patch-запрос: {}-запрос на подтверждение бронирования с id {} пользователем {}.",
                approved, bookingId, ownerId);
        return client.confirmationBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @PathVariable long bookingId) {
        log.info("Get-запрос: запрос на получение бронирования {} пользователем {}.", bookingId, userId);
        return client.getBooking(userId, bookingId);
    }

    private void checkValidBooking(BookingCreationDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start.isEqual(end)) throw new BookingDateException("длительность аренды не может быть нулевой");
        if (end.isBefore(start)) throw new BookingDateException("некорректно указаны сроки начала/конца аренды");
    }
}
