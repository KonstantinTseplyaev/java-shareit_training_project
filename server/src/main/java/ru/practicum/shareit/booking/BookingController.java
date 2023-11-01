package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import java.util.List;

import static ru.practicum.shareit.mapper.MapperUtil.USER_ID_HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(USER_ID_HEADER) long userId,
                                    @RequestBody BookingCreationDto bookingDto) {
        log.info("Post-запрос: запрос на бронирование {} пользователем {}.", bookingDto, userId);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmationBooking(@RequestHeader(USER_ID_HEADER) long ownerId,
                                          @PathVariable long bookingId,
                                          @RequestParam Boolean approved) {
        log.info("Patch-запрос: {}-запрос на подтверждение бронирования с id {} пользователем {}.",
                approved, bookingId, ownerId);
        return bookingService.confirmationBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER_ID_HEADER) long userId,
                                     @PathVariable long bookingId) {
        log.info("Get-запрос: запрос на получение бронирования {} пользователем {}.", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                                   @RequestParam String state,
                                                   @RequestParam int from,
                                                   @RequestParam int size) {
        log.info("Get-запрос: запрос на получение всех бронирований со статусом {} пользователя {}.", state, userId);
        return bookingService.getAllBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwnerId(@RequestHeader(USER_ID_HEADER) long ownerId,
                                                    @RequestParam String state,
                                                    @RequestParam int from,
                                                    @RequestParam int size) {
        log.info("Get-запрос: запрос на получение всех бронирований со статусом {} владельца {}.", state, ownerId);
        return bookingService.getAllBookingsByOwnerId(ownerId, state, from, size);
    }
}
