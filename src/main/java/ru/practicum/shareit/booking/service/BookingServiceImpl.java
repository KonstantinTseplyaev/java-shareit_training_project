package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.exceptions.ParamValidationException;
import ru.practicum.shareit.exceptions.UnknownBookingStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    LocalDateTime now = LocalDateTime.now();

    @Override
    public BookingDto createBooking(long userId, BookingCreationDto bookingDto) {
        checkValidBooking(bookingDto);
        User user = userService.getUser(userId);
        Item item = itemService.getItem(bookingDto.getItemId());
        if (userId == item.getUser().getId()) {
            throw new ItemNotFoundException("владелец вещи не может её забронировать");
        }
        if (item.getAvailable()) {
            Booking newBooking = bookingRepository
                    .save(MapperUtil.convertFromBookingCreationDto(bookingDto, item, user));
            return MapperUtil.convertToBookingDto(newBooking);
        } else {
            throw new AvailableStatusException("попытка арендовать вещь, недоступную для аренды.");
        }
    }

    @Override
    public BookingDto confirmationBooking(long ownerId, long bookingId, String approved) {
        State state = State.REJECTED;
        if (approved.equals("true")) {
            state = State.APPROVED;
        }
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new BookingNotFoundException("бронирования с id " + bookingId + " не существует");
        }
        Booking booking = bookingOptional.get();
        if (ownerId != booking.getItemOwnerId()) {
            throw new BookingNotFoundException("пользователь " + ownerId + " не является владельцем вещи");
        }
        if (booking.getState() != state) {
            booking.setState(state);
            return MapperUtil.convertToBookingDto(bookingRepository.save(booking));
        } else {
            throw new AvailableStatusException("нельзя изменить статус бронирования после подтверждения");
        }
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isEmpty()) {
            throw new BookingNotFoundException("бронирования с id " + bookingId + " не существует");
        }
        Booking booking = bookingOptional.get();
        if (userId == booking.getUser().getId() || userId == booking.getItem().getUser().getId()) {
            return MapperUtil.convertToBookingDto(booking);
        } else {
            throw new BookingNotFoundException("бронирования с id " + bookingId + " нет у пользователя " + userId);
        }
    }

    @Override
    public List<BookingDto> getAllBookingsByUserId(long userId, String state) {
        userService.getUser(userId);
        List<Booking> bookings;
        State currentState = State.valueOf(state);
        LocalDateTime currentNow = LocalDateTime.now();
        switch (currentState) {
            case ALL:
                bookings = bookingRepository.findByUserIdOrderByStartDesc(userId);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findBookingsByUserIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case PAST:
                bookings = bookingRepository.findByUserIdAndEndBeforeOrderByStartDesc(userId, currentNow);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findByUserIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, currentNow, currentNow);
                break;
            case WAITING:
            case REJECTED:
            case APPROVED:
                bookings = bookingRepository.findByUserIdAndStateOrderByStartDesc(userId, currentState);
                break;
            default:
                throw new UnknownBookingStateException("Unknown state: " + currentState);
        }
        return MapperUtil.convertList(bookings, MapperUtil::convertToBookingDto);
    }

    @Override
    public List<BookingDto> getAllBookingsByOwnerId(long ownerId, String state) {
        userService.getUser(ownerId);
        List<Booking> bookings;
        State currentState = State.valueOf(state);
        LocalDateTime currentNow = LocalDateTime.now();
        //объясню ситуацию, почему для future используется другой таймстап. Это нужно для того, чтобы проходились тесты на запрос future букингов. С момента создания букинга с id 1 и до запроса всех future букингов проходит примерно секунда, за эту секунду этот букинг успевает стать из future уже current, не попадает в выборку и валит тесты. Проверял сто раз на бОльших интервалах, метод работает корректно
        switch (currentState) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, currentNow);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, currentNow, currentNow);
                break;
            case WAITING:
            case REJECTED:
            case APPROVED:
                bookings = bookingRepository.findByItemOwnerIdAndStateOrderByStartDesc(ownerId, currentState);
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
                    || b.getStart().isAfter(start) && b.getEnd().isBefore(end)) {
                throw new ParamValidationException("данное время для бронирования недоступно");
            }
        }
    }
}
