package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AvailableStatusException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ParamValidationException;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto getItemById(long itemId, long ownerId) {
        if (!itemRepository.existsById(itemId))
            throw new ItemNotFoundException("вещи с id " + itemId + " не существует");
        List<CommentDto> commentsDto = MapperUtil.convertList(commentRepository.findByItemIdOrderByCreatedDesc(itemId),
                MapperUtil::convertToCommentDto);
        Optional<Item> itemOpt = itemRepository.findByIdAndOwnerId(itemId, ownerId);
        if (itemOpt.isPresent()) {
            Optional<Booking> lastOpt = bookingRepository.findByItemIdAndLastBooking(itemId);
            Optional<Booking> nextOpt = bookingRepository.findByItemIdAndNextBooking(itemId);
            BookingForItemDto lastBooking = null;
            BookingForItemDto nextBooking = null;
            if (lastOpt.isPresent()) lastBooking = MapperUtil.convertToBookingForItemDto(lastOpt.get());
            if (nextOpt.isPresent()) nextBooking = MapperUtil.convertToBookingForItemDto(nextOpt.get());
            return MapperUtil.convertToItemDto(itemOpt.get(), lastBooking, nextBooking, commentsDto);
        }
        return MapperUtil.convertToItemDto(itemRepository.findById(itemId).get(), commentsDto);
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(long ownerId) {
        Map<Long, Item> itemMap = itemRepository.findByOwnerId(ownerId)
                .stream().collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, List<CommentDto>> commentMap = MapperUtil.convertList(commentRepository
                        .findByItemIdIn(itemMap.keySet()), MapperUtil::convertToCommentDto)
                .stream().collect(Collectors.groupingBy(CommentDto::getItemId));
        Map<Long, BookingForItemDto> lastBookingMap = MapperUtil.convertList(bookingRepository
                        .findAllByItemIdAndLastBooking(itemMap.keySet()), MapperUtil::convertToBookingForItemDto)
                .stream().collect(Collectors.toMap(BookingForItemDto::getItemId, Function.identity()));
        Map<Long, BookingForItemDto> nextBookingMap = MapperUtil.convertList(bookingRepository
                        .findAllByItemIdAndNextBooking(itemMap.keySet()), MapperUtil::convertToBookingForItemDto)
                .stream().collect(Collectors.toMap(BookingForItemDto::getItemId, Function.identity()));
        return itemMap.values().stream().map(item -> MapperUtil.convertToItemDto(item, lastBookingMap.get(item.getId()),
                nextBookingMap.get(item.getId()), commentMap.get(item.getId()))).collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemCreationDto itemDto, long ownerId) {
        checkValidItem(itemDto);
        Optional<User> ownerOpt = userRepository.findById(ownerId);
        if (ownerOpt.isEmpty()) throw new UserNotFoundException("пользователя с id " + ownerId + " не существует");
        Item newItem = MapperUtil.convertFromItemCreationDto(itemDto, ownerOpt.get());
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
    public CommentDto addComment(long authorId, long itemId, CommentCreationDto comment) {
        Optional<User> authorOpt = userRepository.findById(authorId);
        if (authorOpt.isEmpty()) throw new UserNotFoundException("пользователя с id " + authorId + " не существует");
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isEmpty()) throw new ItemNotFoundException("вещи с id " + itemId + " не существует");
        boolean isExist = bookingRepository.existsByUserIdAndItemIdAndEndBefore(authorId, itemId, LocalDateTime.now());
        if (isExist) {
            return MapperUtil.convertToCommentDto(commentRepository
                    .save(MapperUtil.convertFromCommentCreationDto(comment, authorOpt.get(), itemOpt.get())));
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

    private Item updateItemFromDtoParam(ItemDto itemDto, long ownerId) {
        Optional<Item> itemOptional = itemRepository.findByIdAndOwnerId(itemDto.getId(), ownerId);
        if (itemOptional.isEmpty())
            throw new UserNotFoundException("вещи с id " + itemDto.getId() + " нет у пользователя " + ownerId);
        Item updatedItem = itemOptional.get();
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
