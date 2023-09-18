package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemStatusException;
import ru.practicum.shareit.exceptions.ValidationUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private long currentId = 0;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItemById(Long id) {
        itemRepository.checkValidId(id);
        return MapperUtil.convertToItemDto(itemRepository.getById(id));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        userRepository.checkUserId(userId);
        List<Item> items = itemRepository.getAllByUserId(userId);
        return MapperUtil.convertList(items, MapperUtil::convertToItemDto);
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long owner) {
        userRepository.checkUserId(owner);
        itemDto.setOwner(owner);
        checkValidItem(itemDto);
        Item newItem = MapperUtil.convertFromItemDto(itemDto);
        newItem.setId(++currentId);
        return MapperUtil.convertToItemDto(itemRepository.create(newItem));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        itemRepository.checkValidId(userId, itemId);
        itemDto.setOwner(userId);
        itemDto.setId(itemId);
        Item item = updateItemFromDtoParam(itemDto);
        Item updateItem = itemRepository.update(item);
        return MapperUtil.convertToItemDto(updateItem);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        String textForSearch = text.toLowerCase(Locale.ROOT);
        List<ItemDto> result = new ArrayList<>();
        if (textForSearch.isEmpty()) {
            return result;
        } else {
            List<Item> items = itemRepository.getItemsByText(textForSearch);
            result = MapperUtil.convertList(items, MapperUtil::convertToItemDto);
        }
        return result;
    }

    private static void checkValidItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getDescription() == null) {
            throw new ValidationUserException("не указаны имя или описание вещи");
        }

        if (itemDto.getAvailable() == null) {
            throw new ItemStatusException("у вещи не указан статус доступа для аренды");
        }
    }

    private Item updateItemFromDtoParam(ItemDto itemDto) {
        Item updatedItem = itemRepository.getById(itemDto.getId());
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
