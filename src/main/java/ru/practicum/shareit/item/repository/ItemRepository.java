package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item getById(Long id);
    List<Item> getAllByUserId(Long userId);
    Item create(Item item);
    Item update(Item item);
    List<Item> getAllItems();
}
