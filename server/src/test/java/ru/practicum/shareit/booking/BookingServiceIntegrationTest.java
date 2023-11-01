/*
package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private User owner;
    private User user;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("John")
                .email("john@example.com")
                .build();

        user = User.builder()
                .name("Peter")
                .email("peter@example.com")
                .build();

        item1 = Item.builder()
                .name("Баскетбольный мяч")
                .description("классный мяч")
                .available(true)
                .owner(owner)
                .build();

        item2 = Item.builder()
                .name("Футбольный мяч")
                .description("классный мяч")
                .available(true)
                .owner(owner)
                .build();

        booking1 = Booking.builder()
                .item(item1)
                .user(user)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .state(State.WAITING)
                .itemOwnerId(owner.getId())
                .build();

        booking2 = Booking.builder()
                .item(item1)
                .user(user)
                .start(LocalDateTime.now().minusHours(5))
                .end(LocalDateTime.now().minusHours(3))
                .state(State.WAITING)
                .itemOwnerId(owner.getId())
                .build();

        booking3 = Booking.builder()
                .item(item2)
                .user(user)
                .start(LocalDateTime.now().minusDays(12))
                .end(LocalDateTime.now().minusDays(8))
                .state(State.WAITING)
                .itemOwnerId(owner.getId())
                .build();

        booking4 = Booking.builder()
                .item(item2)
                .user(user)
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(3))
                .state(State.WAITING)
                .itemOwnerId(owner.getId())
                .build();
    }

    @Test
    void getAllBookingsByUserId_withStateIsWAITING() {
        User newOwner = userRepository.save(owner);
        User newUser = userRepository.save(user);
        item1.setOwner(newOwner);
        item2.setOwner(newOwner);
        Item newItem1 = itemRepository.save(item1);
        Item newItem2 = itemRepository.save(item2);
        booking1.setItem(newItem1);
        booking1.setUser(newUser);
        booking2.setItem(newItem1);
        booking2.setUser(newUser);
        booking3.setItem(newItem2);
        booking3.setUser(newUser);
        booking4.setItem(newItem2);
        booking4.setUser(newUser);
        Booking newBook1 = bookingRepository.save(booking1);
        Booking newBook2 = bookingRepository.save(booking2);
        Booking newBook3 = bookingRepository.save(booking3);
        Booking newBook4 = bookingRepository.save(booking4);

        List<BookingDto> bookingList = MapperUtil.convertList(List.of(newBook1, newBook2, newBook3, newBook4), MapperUtil::convertToBookingDto);
        bookingList.sort(Comparator.comparing(BookingDto::getStart).reversed());

        List<BookingDto> result = bookingService.getAllBookingsByUserId(newUser.getId(), "WAITING", 0, 10);

        assertThat(bookingList, equalTo(result));
    }

    @Test
    void getAllBookingsByUserId_withStateIsPAST() {
        User newOwner = userRepository.save(owner);
        User newUser = userRepository.save(user);
        item1.setOwner(newOwner);
        item2.setOwner(newOwner);
        Item newItem1 = itemRepository.save(item1);
        Item newItem2 = itemRepository.save(item2);
        booking1.setItem(newItem1);
        booking1.setUser(newUser);
        booking2.setItem(newItem1);
        booking2.setUser(newUser);
        booking3.setItem(newItem2);
        booking3.setUser(newUser);
        booking4.setItem(newItem2);
        booking4.setUser(newUser);
        bookingRepository.save(booking1);
        Booking newBook2 = bookingRepository.save(booking2);
        Booking newBook3 = bookingRepository.save(booking3);
        bookingRepository.save(booking4);

        List<BookingDto> bookingList = MapperUtil.convertList(List.of(newBook2, newBook3), MapperUtil::convertToBookingDto);
        bookingList.sort(Comparator.comparing(BookingDto::getStart).reversed());

        List<BookingDto> result = bookingService.getAllBookingsByUserId(newUser.getId(), "PAST", 0, 10);

        assertThat(bookingList, equalTo(result));
    }

    @Test
    void getAllBookingsByOwnerId_withStateIsFUTURE() {
        User newOwner = userRepository.save(owner);
        User newUser = userRepository.save(user);
        item1.setOwner(newOwner);
        item2.setOwner(newOwner);
        Item newItem1 = itemRepository.save(item1);
        Item newItem2 = itemRepository.save(item2);
        booking1.setItem(newItem1);
        booking1.setUser(newUser);
        booking2.setItem(newItem1);
        booking2.setUser(newUser);
        booking3.setItem(newItem2);
        booking3.setUser(newUser);
        booking4.setItem(newItem2);
        booking4.setUser(newUser);
        Booking newBook1 = bookingRepository.save(booking1);
        newBook1.setItemOwnerId(newOwner.getId());
        Booking newBook2 = bookingRepository.save(booking2);
        newBook2.setItemOwnerId(newOwner.getId());
        Booking newBook3 = bookingRepository.save(booking3);
        newBook3.setItemOwnerId(newOwner.getId());
        Booking newBook4 = bookingRepository.save(booking4);
        newBook4.setItemOwnerId(newOwner.getId());

        List<BookingDto> bookingList = MapperUtil.convertList(List.of(newBook1, newBook4), MapperUtil::convertToBookingDto);
        bookingList.sort(Comparator.comparing(BookingDto::getStart).reversed());

        List<BookingDto> result = bookingService.getAllBookingsByOwnerId(newOwner.getId(), "FUTURE", 0, 10);

        assertThat(bookingList, equalTo(result));
    }
}
*/
