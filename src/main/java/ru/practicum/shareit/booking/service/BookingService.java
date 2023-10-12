package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(long userId, BookingCreationDto bookingDto);

    BookingDto confirmationBooking(long ownerId, long bookingId, Boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getAllBookingsByUserId(long userId, String state);

    List<BookingDto> getAllBookingsByOwnerId(long ownerId, String state);
}
