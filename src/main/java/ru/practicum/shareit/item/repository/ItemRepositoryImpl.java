package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.exceptions.ValidationUserException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Long>> userAndItem = new HashMap<>();
    private final Map<Long, Item> itemMap = new HashMap<>();

    @Override
    public Item getById(Long id) {
        return itemMap.get(id);
    }

    @Override
    public List<Item> getAllByUserId(Long userId) {
        List<Item> items = new ArrayList<>();
        for (Long l : userAndItem.get(userId)) {
            items.add(itemMap.get(l));
        }
        return items;
    }

    @Override
    public Item create(Item item) {
        Long owner = item.getOwner();
        Long itemId = item.getId();
        if (userAndItem.containsKey(owner)) userAndItem.get(owner).add(itemId);
        else userAndItem.put(owner, new ArrayList<>(List.of(itemId)));
        itemMap.put(itemId, item);
        return getById(itemId);
    }

    @Override
    public Item update(Item item) {
        itemMap.put(item.getId(), item);
        return getById(item.getId());
    }

    @Override
    public List<Item> getItemsByText(String text) {
        List<Item> result = new ArrayList<>();
        for (Item item : itemMap.values()) {
            if (item.getName().toLowerCase().contains(text)
                    || item.getDescription().toLowerCase().contains(text)) {
                if (item.getAvailable()) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    @Override
    public void checkValidId(Long itemId) {
        if (!itemMap.containsKey(itemId)) {
            throw new ValidationUserException("вещи с id " + itemId + " не существует");
        }
    }

    @Override
    public void checkValidId(Long userId, Long itemId) {
        checkValidId(itemId);
        if (userAndItem.containsKey(userId)) {
            if (!userAndItem.get(userId).contains(itemId)) {
                throw new UserNotFoundException("у пользователя " + userId + " нет вещи с id " + itemId);
            }
        } else {
            throw new UserNotFoundException("пользователь с id " + userId + " не найден");
        }
    }
}
