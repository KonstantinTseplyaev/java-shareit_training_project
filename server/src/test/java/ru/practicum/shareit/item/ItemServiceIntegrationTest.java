/*package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private User owner;
    private User user;
    private Item item;
    private Item item2;
    private Item item3;
    private Booking booking1;
    private Booking booking2;
    private Comment comment;

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

        item = Item.builder()
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

        item3 = Item.builder()
                .name("Волейбольный мяч")
                .description("классный мяч")
                .available(true)
                .owner(owner)
                .build();

        booking1 = Booking.builder()
                .item(item)
                .user(user)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .state(State.WAITING)
                .itemOwnerId(owner.getId())
                .build();

        booking2 = Booking.builder()
                .item(item)
                .user(user)
                .start(LocalDateTime.now().minusHours(5))
                .end(LocalDateTime.now().minusHours(3))
                .state(State.WAITING)
                .itemOwnerId(owner.getId())
                .build();

        comment = Comment.builder()
                .author(user)
                .item(item)
                .text("Спасибо, очень классный мяч")
                .created(LocalDateTime.of(2022, 8, 13, 14, 30))
                .build();
    }

    @Test
    void getItemById_whenUserIsOwner() {
        User newOwner = userRepository.save(owner);
        User newUser = userRepository.save(user);
        item.setOwner(newOwner);
        Item newItem = itemRepository.save(item);
        booking1.setItem(newItem);
        booking1.setUser(newUser);
        booking2.setItem(newItem);
        booking2.setUser(newUser);
        comment.setItem(newItem);
        comment.setAuthor(newUser);
        Booking firstBooking = bookingRepository.save(booking1);
        firstBooking.setState(State.APPROVED);
        Comment newComment = commentRepository.save(comment);
        Booking secondBooking = bookingRepository.save(booking2);
        secondBooking.setState(State.APPROVED);

        ItemDto result = itemService.getItemById(newItem.getId(), newOwner.getId());

        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getOwner(), equalTo(owner.getId()));
        assertThat(result.getComments(), equalTo(MapperUtil.convertList(List.of(newComment), MapperUtil::convertToCommentDto)));
        assertThat(result.getLastBooking(), equalTo(MapperUtil.convertToBookingForItemDto(secondBooking)));
        assertThat(result.getNextBooking(), equalTo(MapperUtil.convertToBookingForItemDto(firstBooking)));
    }

    @Test
    void getItemById_whenUserIsNotOwner() {
        User newOwner = userRepository.save(owner);
        User newUser = userRepository.save(user);
        item.setOwner(newOwner);
        Item newItem = itemRepository.save(item);
        booking1.setItem(newItem);
        booking1.setUser(newUser);
        booking2.setItem(newItem);
        booking2.setUser(newUser);
        comment.setItem(newItem);
        comment.setAuthor(newUser);
        Booking firstBooking = bookingRepository.save(booking1);
        firstBooking.setState(State.APPROVED);
        Comment newComment = commentRepository.save(comment);
        Booking secondBooking = bookingRepository.save(booking2);
        secondBooking.setState(State.APPROVED);

        ItemDto result = itemService.getItemById(newItem.getId(), newUser.getId());

        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getOwner(), equalTo(owner.getId()));
        assertThat(result.getComments(), equalTo(MapperUtil.convertList(List.of(newComment), MapperUtil::convertToCommentDto)));
        assertThat(result.getLastBooking(), nullValue());
        assertThat(result.getNextBooking(), nullValue());
    }

    @Test
    void getAllItemsByOwnerId() {
        User newOwner = userRepository.save(owner);
        User newUser = userRepository.save(user);
        item.setOwner(newOwner);
        item2.setOwner(newOwner);
        item3.setOwner(newOwner);
        Item newItem = itemRepository.save(item);
        Item newItem2 = itemRepository.save(item2);
        itemRepository.save(item3);
        booking1.setItem(newItem);
        booking1.setUser(newUser);
        booking2.setItem(newItem);
        booking2.setUser(newUser);
        comment.setItem(newItem);
        comment.setAuthor(newUser);
        Booking firstBooking = bookingRepository.save(booking1);
        firstBooking.setState(State.APPROVED);
        Comment newComment = commentRepository.save(comment);
        Booking secondBooking = bookingRepository.save(booking2);
        secondBooking.setState(State.APPROVED);

        List<ItemDto> result = itemService.getAllItemsByOwnerId(newOwner.getId(), 0, 2);

        assertThat(result, hasSize(2));
        assertThat(result, hasItem(MapperUtil.convertToItemDto(newItem, MapperUtil.convertToBookingForItemDto(secondBooking),
                MapperUtil.convertToBookingForItemDto(firstBooking), MapperUtil.convertList(List.of(newComment), MapperUtil::convertToCommentDto))));
        assertThat(result, hasItem(MapperUtil.convertToItemDto(newItem2)));
    }
}*/
