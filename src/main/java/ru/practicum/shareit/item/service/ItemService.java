package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long id);

    List<ItemDto> getAllItemsByUserId(Long userId);

    ItemDto createItem(ItemDto itemDto, Long owner);

    ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId);

    List<ItemDto> searchItemsByText(String text);
}
