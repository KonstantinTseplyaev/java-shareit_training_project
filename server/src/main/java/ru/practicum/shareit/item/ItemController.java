package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.mapper.MapperUtil.USER_ID_HEADER;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get-запрос: получение вещи по id {}.", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) long ownerId,
                                             @RequestParam int from,
                                             @RequestParam int size) {
        log.info("Get-запрос: получение всех вещей пользователя с id {}.", ownerId);
        return itemService.getAllItemsByOwnerId(ownerId, from, size);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(USER_ID_HEADER) long userId,
                              @RequestBody ItemCreationDto itemDto) {
        log.info("Post-запрос: создание нового итема {} пользователем {}.", itemDto, userId);
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER) long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Patch-запрос: обновление существующего итема с id {} пользователем {}.", itemId, userId);
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam String text,
                                           @RequestParam int from,
                                           @RequestParam int size) {
        log.info("Get-запрос: поиск вещи по тексту {} в названии или описании.", text);
        return itemService.searchItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentCreationDto comment) {
        log.info("Post-запрос: добавление комментария от пользователя {} к вещи {}.", userId, itemId);
        return itemService.addComment(userId, itemId, comment);
    }
}
