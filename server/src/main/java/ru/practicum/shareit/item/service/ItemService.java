package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getAllItemsByOwnerId(long ownerId, int from, int size);

    ItemDto createItem(ItemCreationDto itemDto, long owner);

    ItemDto updateItem(ItemDto itemDto, long userId, long itemId);

    CommentDto addComment(long userId, long itemId, CommentCreationDto comment);

    List<ItemDto> searchItemsByText(String text, int from, int size);
}
