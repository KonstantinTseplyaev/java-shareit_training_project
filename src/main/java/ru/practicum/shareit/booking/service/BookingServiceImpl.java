package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AvailableStatusException;
import ru.practicum.shareit.exceptions.BookingDateException;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.PaginationException;
import ru.practicum.shareit.exceptions.ParamValidationException;
import ru.practicum.shareit.exceptions.UnknownBookingStateException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(long userId, BookingCreationDto bookingDto) {
        checkValidBooking(bookingDto);
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) throw new UserNotFoundException("пользователя с id " + userId + " не существует");
        Optional<Item> itemOpt = itemRepository.findById(bookingDto.getItemId());
        if (itemOpt.isEmpty())
            throw new ItemNotFoundException("вещи с id " + bookingDto.getItemId() + " не существует");
        if (userId == itemOpt.get().getOwner().getId()) {
            throw new ItemNotFoundException("владелец вещи не может её забронировать");
        }
        if (itemOpt.get().getAvailable()) {
            Booking newBooking = bookingRepository
                    .save(MapperUtil.convertFromBookingCreationDto(bookingDto, itemOpt.get(), userOpt.get()));
            return MapperUtil.convertToBookingDto(newBooking);
        } else {
            throw new AvailableStatusException("попытка арендовать вещь, недоступную для аренды.");
        }
    }

    @Override
    public BookingDto confirmationBooking(long ownerId, long bookingId, Boolean approved) {
        if (!userRepository.existsById(ownerId))
            throw new UserNotFoundException("пользователя с id " + ownerId + " не существует");
        State state = State.REJECTED;
        if (approved) state = State.APPROVED;
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        Booking booking = bookingOptional.orElseThrow(() ->
                new BookingNotFoundException("бронирования с id " + bookingId + " не существует"));
        if (ownerId != booking.getItemOwnerId()) {
            throw new BookingNotFoundException("пользователь " + ownerId + " не является владельцем вещи");
        }
        if (booking.getState() == State.WAITING) {
            booking.setState(state);
            return MapperUtil.convertToBookingDto(bookingRepository.save(booking));
        } else {
            throw new AvailableStatusException("нельзя забронировать вещь со статусом " + booking.getState());
        }
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("пользователя с id " + userId + " не существует");
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        Booking booking = bookingOptional.orElseThrow(() ->
                new BookingNotFoundException("бронирования с id " + bookingId + " не существует"));
        if (userId == booking.getUser().getId() || userId == booking.getItem().getOwner().getId()) {
            return MapperUtil.convertToBookingDto(booking);
        } else {
            throw new BookingNotFoundException("бронирования с id " + bookingId + " нет у пользователя " + userId);
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(long userId, String state, int from, int size) {
        if (!userRepository.existsById(userId))
            throw new UserNotFoundException("пользователя с id " + userId + " не существует");
        checkPaginationParams(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> bookings;
        State currentState = State.valueOf(state);
        LocalDateTime now = LocalDateTime.now();
        switch (currentState) {
            case ALL:
                bookings = bookingRepository.findByUserIdOrderByStartDesc(userId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findBookingsByUserIdAndStartAfterOrderByStartDesc(userId, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findByUserIdAndEndBeforeOrderByStartDesc(userId, now, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findByUserIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, now, now, pageable);
                break;
            case WAITING:
            case REJECTED:
            case APPROVED:
                bookings = bookingRepository.findByUserIdAndStateOrderByStartDesc(userId, currentState, pageable);
                break;
            default:
                throw new UnknownBookingStateException("Unknown state: " + currentState);
        }
        return MapperUtil.convertList(bookings, MapperUtil::convertToBookingDto);
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(long ownerId, String state, int from, int size) {
        if (!userRepository.existsById(ownerId))
            throw new UserNotFoundException("пользователя с id " + ownerId + " не существует");
        checkPaginationParams(from, size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Booking> bookings;
        State currentState = State.valueOf(state);
        LocalDateTime now = LocalDateTime.now();
        switch (currentState) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository
                        .findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now, pageable);
                break;
            case WAITING:
            case REJECTED:
            case APPROVED:
                bookings = bookingRepository
                        .findByItemOwnerIdAndStateOrderByStartDesc(ownerId, currentState, pageable);
                break;
            default:
                throw new UnknownBookingStateException("Unknown state: " + currentState);
        }
        return MapperUtil.convertList(bookings, MapperUtil::convertToBookingDto);
    }

    private void checkValidBooking(BookingCreationDto bookingDto) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start.isEqual(end)) throw new BookingDateException("длительность аренды не может быть нулевой");
        if (end.isBefore(start)) throw new BookingDateException("некорректно указаны сроки начала/конца аренды");
        findTimeIntersections(bookingDto.getItemId(), start, end);
    }

    private void findTimeIntersections(long itemId, LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = bookingRepository.findByItemId(itemId);
        for (Booking b : bookings) {
            if (b.getStart().isBefore(start) && b.getEnd().isAfter(end)
                    || b.getStart().isAfter(start) && b.getEnd().isBefore(end)
                    || b.getStart().isEqual(start) && b.getEnd().isEqual(end)) {
                throw new ParamValidationException("данное время для бронирования недоступно");
            }
        }
    }

    private void checkPaginationParams(int from, int size) {
        if (from < 0 || size < 1) throw new PaginationException("неверные параметры пагинации");
    }
}
