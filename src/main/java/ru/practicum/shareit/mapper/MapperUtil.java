package ru.practicum.shareit.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class MapperUtil {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static <R, E> List<R> convertList(List<E> list, Function<E, R> converter) {
        return list.stream().map(converter).collect(Collectors.toList());
    }

    public static UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public static User convertFromUserDto(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static ItemDto convertToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .build();
    }

    public static ItemForBookingDto convertToItemForBookingDto(Item item) {
        return modelMapper.map(item, ItemForBookingDto.class);
    }

    public static ItemDto convertToItemDto(Item item, BookingForItemDto last,
                                           BookingForItemDto next, List<CommentDto> comments) {
        ItemDto itemDto = convertToItemDto(item);
        itemDto.setLastBooking(last);
        itemDto.setNextBooking(next);
        itemDto.setComments(comments);
        return itemDto;
    }

    public static ItemDto convertToItemDto(Item item, List<CommentDto> comments) {
        ItemDto itemDto = convertToItemDto(item);
        itemDto.setComments(comments);
        return itemDto;
    }

    public static Item convertFromItemCreationDto(ItemCreationDto itemCreationDto, User owner) {
        return Item.builder()
                .name(itemCreationDto.getName())
                .description(itemCreationDto.getDescription())
                .available(itemCreationDto.getAvailable())
                .owner(owner)
                .build();
    }

    public static Booking convertFromBookingCreationDto(BookingCreationDto bookingCreationDto, Item item, User user) {
        return Booking.builder()
                .item(item)
                .user(user)
                .start(bookingCreationDto.getStart())
                .end(bookingCreationDto.getEnd())
                .state(State.WAITING)
                .itemOwnerId(item.getOwner().getId())
                .build();
    }

    public static BookingDto convertToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .item(convertToItemForBookingDto(booking.getItem()))
                .booker(convertToUserDto(booking.getUser()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getState())
                .build();
    }

    public static BookingForItemDto convertToBookingForItemDto(Booking booking) {
        return BookingForItemDto
                .builder()
                .id(booking.getId())
                .bookerId(booking.getUser().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getState())
                .itemId(booking.getItem().getId())
                .build();
    }

    public static CommentDto convertToCommentDto(Comment comment) {
        return CommentDto.builder()
                .authorName(comment.getAuthor().getName())
                .itemId(comment.getItem().getId())
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public static Comment convertFromCommentCreationDto(CommentCreationDto commentDto, User author, Item item) {
        return Comment.builder()
                .item(item)
                .author(author)
                .text(commentDto.getText())
                .created(LocalDateTime.now())
                .build();
    }
}
