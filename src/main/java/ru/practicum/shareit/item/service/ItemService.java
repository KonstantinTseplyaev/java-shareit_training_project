package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(long itemId, long userId);

    Item getItem(Long id);

    List<ItemDto> getAllItemsByUserId(long userId);

    ItemDto createItem(ItemCreationDto itemDto, long owner);

    ItemDto updateItem(ItemDto itemDto, long userId, long itemId);

    CommentDto addComment(long userId, long itemId, CommentCreationDto comment);

    List<ItemDto> searchItemsByText(String text);
}
