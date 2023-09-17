package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserRepositoryImpl userRepository;
    private final ItemRepositoryImpl itemRepository;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Get-запрос: получение вещи по id {}.", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        userRepository.checkUserId(userId);
        log.info("Get-запрос: получение всех вещей пользователя с id {}.", userId);
        return itemService.getAllItemsByUserId(userId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Post-запрос: создание нового итема {} пользователем {}.", itemDto, userId);
        userRepository.checkUserId(userId);
        itemDto.setOwner(userId);
        return itemService.createItem(itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Patch-запрос: обновление существующего итема с id {} пользователем {}.", itemId, userId);
        itemRepository.checkValidId(userId, itemId);
        itemDto.setOwner(userId);
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text) {
        String textForSearch = text.toLowerCase(Locale.ROOT);
        log.info("Get-запрос: поиск вещи по тексту {} в названии или описании.", textForSearch);
        return itemService.searchItemsByText(textForSearch);
    }
}
