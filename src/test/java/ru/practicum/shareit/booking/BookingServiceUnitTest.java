package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.AvailableStatusException;
import ru.practicum.shareit.exceptions.BookingNotFoundException;
import ru.practicum.shareit.exceptions.ParamValidationException;
import ru.practicum.shareit.exceptions.UnknownBookingStateException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking1;
    private Booking booking2;
    private BookingCreationDto creationB1;
    private BookingDto booking1Dto;
    private BookingDto booking2Dto;
    private Item item1;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Jane")
                .email("jane@example.com")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("Баскетбольный мяч")
                .description("классный мяч")
                .available(true)
                .owner(user1)
                .build();

        booking1 = Booking.builder()
                .id(1L)
                .item(item1)
                .user(user2)
                .start(LocalDateTime.of(2022, 8, 11, 13, 0))
                .end(LocalDateTime.of(2022, 8, 12, 13, 0))
                .state(State.WAITING)
                .itemOwnerId(user1.getId())
                .build();

        booking2 = Booking.builder()
                .id(3L)
                .item(item1)
                .user(user2)
                .start(LocalDateTime.of(2023, 8, 11, 13, 0))
                .end(LocalDateTime.of(2023, 8, 12, 13, 0))
                .state(State.WAITING)
                .itemOwnerId(user1.getId())
                .build();

        creationB1 = BookingCreationDto.builder()
                .itemId(item1.getId())
                .start(LocalDateTime.of(2022, 8, 11, 13, 0))
                .end(LocalDateTime.of(2022, 8, 12, 13, 0))
                .build();

        booking1Dto = BookingDto.builder()
                .id(1L)
                .item(new ItemForBookingDto(1L, "Баскетбольный мяч", "классный мяч"))
                .booker(new UserDto(2L, "jane@example.com", "Jane"))
                .start(LocalDateTime.of(2022, 8, 11, 13, 0))
                .end(LocalDateTime.of(2022, 8, 12, 13, 0))
                .status(State.WAITING)
                .build();

        booking2Dto = BookingDto.builder()
                .id(3L)
                .item(new ItemForBookingDto(1L, "Баскетбольный мяч", "классный мяч"))
                .booker(new UserDto(2L, "jane@example.com", "Jane"))
                .start(LocalDateTime.of(2023, 8, 11, 13, 0))
                .end(LocalDateTime.of(2023, 8, 12, 13, 0))
                .status(State.WAITING)
                .build();
    }

    @Test
    void createBooking_withCorrectData() {
        when(bookingRepository.findByItemId(item1.getId()))
                .thenReturn(new ArrayList<>());
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.findById(creationB1.getItemId()))
                .thenReturn(Optional.of(item1));
        when(bookingRepository.save(MapperUtil.convertFromBookingCreationDto(creationB1, item1, user2)))
                .thenReturn(booking1);

        BookingDto result = bookingService.createBooking(user2.getId(), creationB1);

        assertEquals(booking1Dto.getId(), result.getId());
        assertEquals(booking1Dto.getItem(), result.getItem());
        assertEquals(booking1Dto.getBooker(), result.getBooker());
        assertEquals(booking1Dto.getStart(), result.getStart());
        assertEquals(booking1Dto.getEnd(), result.getEnd());
        assertEquals(booking1Dto.getStatus(), result.getStatus());
    }

    @Test
    void createBooking_withIncorrectTime() {
        when(bookingRepository.findByItemId(item1.getId()))
                .thenReturn(List.of(booking1));

        assertThatThrownBy(() -> bookingService.createBooking(user2.getId(), creationB1)).isInstanceOf(ParamValidationException.class)
                .hasMessageContaining("данное время для бронирования недоступно");
    }

    @Test
    void createBooking_withIncorrectUser() {
        when(bookingRepository.findByItemId(item1.getId()))
                .thenReturn(new ArrayList<>());
        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(user2.getId(), creationB1)).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("пользователя с id " + user2.getId() + " не существует");
    }

    @Test
    void confirmationBooking_withCorrectOwner() {
        when(userRepository.existsById(user1.getId()))
                .thenReturn(true);
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1));
        when(bookingRepository.save(booking1))
                .thenReturn(booking1);

        BookingDto result = bookingService.confirmationBooking(user1.getId(), booking1.getId(), true);

        booking1Dto.setStatus(State.APPROVED);

        assertEquals(booking1Dto, result);
    }

    @Test
    void confirmationBooking_withIncorrectOwner() {
        when(userRepository.existsById(user1.getId()))
                .thenReturn(false);

        assertThatThrownBy(() -> bookingService.confirmationBooking(user1.getId(), booking1.getId(), true))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("пользователя с id " + user1.getId() + " не существует");
    }

    @Test
    void confirmationBooking_withIncorrectBookingId() {
        when(userRepository.existsById(user2.getId()))
                .thenReturn(true);
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.confirmationBooking(user2.getId(), booking1.getId(), true))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("бронирования с id " + booking1.getId() + " не существует");
    }

    @Test
    void confirmationBooking_withIncorrectStatus() {
        when(userRepository.existsById(user1.getId()))
                .thenReturn(true);
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1));
        booking1.setState(State.APPROVED);

        assertThatThrownBy(() -> bookingService.confirmationBooking(user1.getId(), booking1.getId(), true))
                .isInstanceOf(AvailableStatusException.class)
                .hasMessageContaining("нельзя забронировать вещь со статусом " + booking1.getState());
    }

    @Test
    void getBookingById_withCorrectId() {
        when(userRepository.existsById(user1.getId()))
                .thenReturn(true);
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1));

        BookingDto result = bookingService.getBookingById(user1.getId(), booking1.getId());

        assertEquals(booking1Dto, result);
    }

    @Test
    void getBookingById_whenUserDoesntHaveBooking() {
        when(userRepository.existsById(3L))
                .thenReturn(true);
        when(bookingRepository.findById(booking1.getId()))
                .thenReturn(Optional.of(booking1));

        assertThatThrownBy(() -> bookingService.getBookingById(3L, booking1.getId()))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("бронирования с id " + booking1.getId() + " нет у пользователя " + 3L);
    }

    @Test
    void getAllBookingsByUserId_withStateWAITING() {
        List<Booking> bookings = List.of(booking1, booking2);

        when(userRepository.existsById(user2.getId()))
                .thenReturn(true);
        when(bookingRepository.findByUserIdAndStateOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingsDto = List.of(booking1Dto, booking2Dto);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(user2.getId(), "WAITING", 0, 20);

        assertEquals(bookingsDto, result);
    }

    @Test
    void getAllBookingsByUserId_withStateFUTURE() {
        List<Booking> bookings = List.of(booking1, booking2);

        when(userRepository.existsById(user2.getId()))
                .thenReturn(true);
        when(bookingRepository.findBookingsByUserIdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingsDto = List.of(booking1Dto, booking2Dto);

        List<BookingDto> result = bookingService.getAllBookingsByUserId(user2.getId(), "FUTURE", 0, 20);

        assertEquals(bookingsDto, result);
    }

    @Test
    void getAllBookingsByOwnerId_withStateWAITING() {
        List<Booking> bookings = List.of(booking1, booking2);

        when(userRepository.existsById(user1.getId()))
                .thenReturn(true);
        when(bookingRepository.findByItemOwnerIdAndStateOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(bookings);

        List<BookingDto> bookingsDto = List.of(booking1Dto, booking2Dto);

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(user1.getId(), "WAITING", 0, 20);

        assertEquals(bookingsDto, result);
    }

    @Test
    void getAllBookingsByOwnerId_withUnknownState() {
        when(userRepository.existsById(user1.getId()))
                .thenReturn(true);

        assertThatThrownBy(() -> bookingService.getAllBookingsByOwnerId(user1.getId(), "UNSUPPORTED_STATUS", 0, 20))
                .isInstanceOf(UnknownBookingStateException.class)
                .hasMessageContaining("Unknown state: UNSUPPORTED_STATUS");
    }
}
