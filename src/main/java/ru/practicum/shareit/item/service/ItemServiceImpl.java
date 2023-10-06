package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AvailableStatusException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ParamValidationException;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        Item item = getItem(itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentsDto = MapperUtil.convertList(comments, MapperUtil::convertToCommentDto);
        if (userId == item.getUser().getId()) {
            Optional<Booking> lastOpt = bookingRepository.findByItemIdAndLastBooking(itemId);
            Optional<Booking> nextOpt = bookingRepository.findByItemIdAndNextBooking(itemId);
            BookingForItemDto lastBooking = null;
            BookingForItemDto nextBooking = null;
            if (lastOpt.isPresent()) lastBooking = MapperUtil.convertToBookingForItemDto(lastOpt.get());
            if (nextOpt.isPresent()) nextBooking = MapperUtil.convertToBookingForItemDto(nextOpt.get());
            return MapperUtil.convertToItemDto(item, lastBooking, nextBooking, commentsDto);
        }
        return MapperUtil.convertToItemDto(item, commentsDto);
    }

    @Override
    public Item getItem(Long id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) return itemOptional.get();
        else throw new UserNotFoundException("вещи с id " + id + " не существует");
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(long userId) {
        List<Item> items = itemRepository.findByUserIdOrderById(userId);
        List<ItemDto> itemsListDto = new ArrayList<>();
        for (Item item : items) {
            List<Comment> comments = commentRepository.findByItemId(item.getId());
            List<CommentDto> commentsDto = MapperUtil.convertList(comments, MapperUtil::convertToCommentDto);
            if (item.getUser().getId() == userId) {
                Optional<Booking> lastOpt = bookingRepository.findByItemIdAndLastBooking(item.getId());
                Optional<Booking> nextOpt = bookingRepository.findByItemIdAndNextBooking(item.getId());
                BookingForItemDto lastBooking = null;
                BookingForItemDto nextBooking = null;
                if (lastOpt.isPresent()) lastBooking = MapperUtil.convertToBookingForItemDto(lastOpt.get());
                if (nextOpt.isPresent()) nextBooking = MapperUtil.convertToBookingForItemDto(nextOpt.get());
                itemsListDto.add(MapperUtil.convertToItemDto(item, lastBooking, nextBooking, commentsDto));
            } else itemsListDto.add(MapperUtil.convertToItemDto(item, commentsDto));
        }
        return itemsListDto;
    }

    @Override
    public ItemDto createItem(ItemCreationDto itemDto, long ownerId) {
        checkValidItem(itemDto);
        User owner = userService.getUser(ownerId);
        Item newItem = MapperUtil.convertFromItemCreationDto(itemDto, owner);
        return MapperUtil.convertToItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) {
        itemDto.setId(itemId);
        Item item = updateItemFromDtoParam(itemDto, userId);
        Item updateItem = itemRepository.save(item);
        return MapperUtil.convertToItemDto(updateItem);
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentCreationDto comment) {
        User user = userService.getUser(userId);
        Item item = getItem(itemId);
        boolean isExist = bookingRepository.existsByUserIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now());
        if (isExist) {
            return MapperUtil.convertToCommentDto(commentRepository
                    .save(Comment.builder().item(item).user(user).text(comment.getText())
                            .created(comment.getCreated()).build()));
        } else throw new AvailableStatusException("нельзя оставлять комментарии к вещи, которую не бронировали");
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        String textForSearch = text.toUpperCase(Locale.ROOT);
        List<ItemDto> result = new ArrayList<>();
        if (textForSearch.isEmpty()) {
            return result;
        } else {
            List<Item> items = itemRepository.findByNameOrDescriptionContaining(textForSearch);
            result = MapperUtil.convertList(items, MapperUtil::convertToItemDto);
        }
        return result;
    }

    private static void checkValidItem(ItemCreationDto itemDto) {
        if (itemDto.getName() == null || itemDto.getDescription() == null) {
            throw new ParamValidationException("не указаны имя или описание вещи");
        }

        if (itemDto.getAvailable() == null) {
            throw new AvailableStatusException("у вещи не указан статус доступа для аренды");
        }
    }

    private Item updateItemFromDtoParam(ItemDto itemDto, long userId) {
        Optional<Item> itemOptional = itemRepository.findById(itemDto.getId());
        if (itemOptional.isEmpty())
            throw new UserNotFoundException("вещи с id " + itemDto.getId() + " не существует");
        Item updatedItem = itemOptional.get();
        if (updatedItem.getUser().getId() != userId) {
            throw new RuntimeException();
        }
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        return updatedItem;
    }
}
