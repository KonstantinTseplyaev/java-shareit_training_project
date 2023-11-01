package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Booking booking1;

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

        item2 = Item.builder()
                .id(2L)
                .name("скрипка")
                .description("очень хорошая скрипка")
                .available(true)
                .owner(user2)
                .build();

        comment1 = Comment.builder()
                .id(1L)
                .author(user2)
                .item(item1)
                .text("Спасибо, очень классный мяч")
                .created(LocalDateTime.of(2022, 8, 13, 14, 30))
                .build();

        booking1 = Booking.builder()
                .id(1L)
                .item(item1)
                .user(user2)
                .start(LocalDateTime.of(2022, 8, 11, 13, 0))
                .end(LocalDateTime.of(2022, 8, 12, 13, 0))
                .state(State.APPROVED)
                .itemOwnerId(user1.getId())
                .build();
    }

    @Test
    void getItemById_forOwnerWhenItemIsExist() {
        long itemId = item1.getId();
        when(itemRepository.existsById(itemId))
                .thenReturn(true);
        when(commentRepository.findByItemIdOrderByCreatedDesc(itemId))
                .thenReturn(List.of(comment1));
        when(itemRepository.findByIdAndOwnerId(itemId, user1.getId()))
                .thenReturn(Optional.of(item1));
        when(bookingRepository.findByItemIdAndLastBooking(itemId))
                .thenReturn(Optional.of(booking1));
        when(bookingRepository.findByItemIdAndNextBooking(itemId))
                .thenReturn(Optional.empty());

        ItemDto result = itemService.getItemById(itemId, user1.getId());

        assertEquals(itemId, result.getId());
        assertEquals(item1.getName(), result.getName());
        assertEquals(user1.getId(), result.getOwner());
        assertEquals(MapperUtil.convertList(List.of(comment1), MapperUtil::convertToCommentDto), result.getComments());
        assertEquals(MapperUtil.convertToBookingForItemDto(booking1), result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void getItemById_forUserWhenItemIsExist() {
        long itemId = item1.getId();
        when(itemRepository.existsById(itemId))
                .thenReturn(true);
        when(commentRepository.findByItemIdOrderByCreatedDesc(itemId))
                .thenReturn(List.of(comment1));
        when(itemRepository.findByIdAndOwnerId(itemId, user2.getId()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item1));

        ItemDto result = itemService.getItemById(itemId, user2.getId());

        assertEquals(itemId, result.getId());
        assertEquals(item1.getName(), result.getName());
        assertEquals(user1.getId(), result.getOwner());
        assertEquals(MapperUtil.convertList(List.of(comment1), MapperUtil::convertToCommentDto), result.getComments());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void getItemById_withIncorrectId() {
        when(itemRepository.existsById(100L))
                .thenReturn(false);

        assertThatThrownBy(() -> itemService.getItemById(100L, user1.getId())).isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("вещи с id " + 100L + " не существует");
    }

    @Test
    void getAllItemsByOwnerId() {
        Item item = Item.builder().id(3L).name("Приставка PS5").description("эксклюзив!!").available(true).owner(user1).build();
        when(itemRepository.findByOwnerId(user1.getId(), PageRequest.of(0, 10)))
                .thenReturn(List.of(item1, item));
        when(commentRepository.findByItemIdIn(Set.of(item1.getId(), item.getId())))
                .thenReturn(List.of(comment1));
        when(bookingRepository.findAllByItemIdAndLastBooking(Set.of(item1.getId(), item.getId())))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findAllByItemIdAndNextBooking(Set.of(item1.getId(), item.getId())))
                .thenReturn(new ArrayList<>());

        List<ItemDto> result = itemService.getAllItemsByOwnerId(user1.getId(), 2, 10);

        assertEquals(2, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item.getId(), result.get(1).getId());
        assertEquals(item1.getName(), result.get(0).getName());
        assertEquals(item.getName(), result.get(1).getName());
        assertEquals(user1.getId(), result.get(0).getOwner());
        assertEquals(user1.getId(), result.get(1).getOwner());
        assertEquals(1, result.get(0).getComments().size());
        assertNull(result.get(1).getComments());
        assertEquals(MapperUtil.convertToBookingForItemDto(booking1), result.get(0).getLastBooking());
        assertNull(result.get(1).getLastBooking());
        assertNull(result.get(0).getNextBooking());
        assertNull(result.get(1).getNextBooking());
    }

    @Test
    void createItem_whenItemIsValid() {
        item2.setRequestId(1L);
        ItemCreationDto itemDto = ItemCreationDto.builder()
                .name("скрипка")
                .description("очень хорошая скрипка")
                .available(true)
                .requestId(1L)
                .build();

        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));
        when(requestRepository.existsById(itemDto.getRequestId()))
                .thenReturn(true);
        when(itemRepository.save(MapperUtil.convertFromItemCreationDto(itemDto, user2)))
                .thenReturn(item2);

        ItemDto result = itemService.createItem(itemDto, user2.getId());

        assertEquals(item2.getId(), result.getId());
        assertEquals(item2.getName(), result.getName());
        assertEquals(user2.getId(), result.getOwner());
        assertNull(result.getComments());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void createItem_whenItemHasNotValidOwner() {
        item2.setRequestId(1L);
        ItemCreationDto itemDto = ItemCreationDto.builder()
                .name("скрипка")
                .description("очень хорошая скрипка")
                .available(true)
                .requestId(1L)
                .build();

        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(itemDto, user2.getId())).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("пользователя с id " + user2.getId() + " не существует");
    }

    @Test
    void updateItem_withValidParams() {
        ItemDto update = ItemDto.builder().name("updateName").build();
        Item updateItem = Item.builder()
                .id(1L)
                .name("updateName")
                .description("классный мяч")
                .available(true)
                .owner(user1)
                .build();
        when(itemRepository.findByIdAndOwnerId(item1.getId(), user1.getId()))
                .thenReturn(Optional.of(item1));
        when(itemRepository.save(updateItem))
                .thenReturn(updateItem);

        ItemDto result = itemService.updateItem(update, user1.getId(), item1.getId());

        assertEquals(updateItem.getId(), result.getId());
        assertEquals(updateItem.getName(), result.getName());
        assertEquals(updateItem.getDescription(), result.getDescription());
    }

    @Test
    void addComment() {
        CommentCreationDto creationComment = new CommentCreationDto("new comment");
        Comment comment = Comment.builder()
                .id(10L)
                .author(user1)
                .item(item2)
                .text("new comment")
                .created(LocalDateTime.now())
                .build();
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(itemRepository.findById(item2.getId()))
                .thenReturn(Optional.of(item2));
        when(bookingRepository.existsByUserIdAndItemIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto result = itemService.addComment(user1.getId(), item2.getId(), creationComment);

        assertEquals(comment.getItem().getId(), result.getItemId());
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getAuthor().getName(), result.getAuthorName());
        assertEquals(comment.getCreated(), result.getCreated());
    }

    @Test
    void searchItemsByText() {
        String text = "search text";
        when(itemRepository.findByNameOrDescriptionContaining(text.toUpperCase(Locale.ROOT), PageRequest.of(0, 10)))
                .thenReturn(List.of(item1, item2));

        List<ItemDto> result = itemService.searchItemsByText(text, 0, 10);

        assertEquals(2, result.size());
        assertEquals(item1.getId(), result.get(0).getId());
        assertEquals(item2.getId(), result.get(1).getId());
        assertEquals(item1.getName(), result.get(0).getName());
        assertEquals(item2.getName(), result.get(1).getName());
    }
}
