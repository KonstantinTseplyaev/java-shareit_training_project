package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemAvailableException;
import ru.practicum.shareit.exceptions.RequestParamException;
import ru.practicum.shareit.exceptions.ValidationUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.MapperUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private long currentId = 0;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto getItemById(Long id) {
        return MapperUtil.convertToItemDto(itemRepository.getById(id));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        List<Item> items = itemRepository.getAllByUserId(userId);
        return MapperUtil.convertList(items, MapperUtil::convertToItemDto);
    }

    @Override
    public ItemDto createItem(ItemDto itemDto) {
        checkValidItem(itemDto);
        Item newItem = MapperUtil.convertFromItemDto(itemDto);
        newItem.setId(++currentId);
        return MapperUtil.convertToItemDto(itemRepository.create(newItem));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto) {
        Item item = updateItemFromDtoParam(itemDto);
        Item updateItem = itemRepository.update(item);
        return MapperUtil.convertToItemDto(updateItem);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        checkTextForSearch(text);
        List<Item> items = itemRepository.getAllItems();
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            if (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text)) {
                if (item.getAvailable()) {
                    result.add(MapperUtil.convertToItemDto(item));
                }
            }
        }
        return result;
    }

    private void checkValidItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getDescription() == null) {
            throw new ValidationUserException("не указаны имя или описание вещи");
        }

        if (itemDto.getAvailable() == null) {
            throw new ItemAvailableException("у вещи не указан статус доступа для аренды");
        }
    }

    private void checkTextForSearch(String text) {
        if (text.isBlank()) {
            throw new RequestParamException("текст для поиска вещи не может быть пустым");
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
