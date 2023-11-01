package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.client.BaseClient.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient client;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId,
                                              @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get-запрос: получение вещи по id {}.", itemId);
        return client.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) long ownerId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("Get-запрос: получение всех вещей пользователя с id {}.", ownerId);
        return client.getItemsByUserId(ownerId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) long userId,
                                             @Valid @RequestBody ItemCreationDto itemDto) {
        log.info("Post-запрос: создание нового итема {} пользователем {}.", itemDto, userId);
        return client.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER) long userId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Patch-запрос: обновление существующего итема с id {} пользователем {}.", itemId, userId);
        return client.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam String text,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "20") @Positive int size,
                                                    @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get-запрос: поиск вещи по тексту {} в названии или описании.", text);
        return client.getItemByText(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID_HEADER) long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentCreationDto comment) {
        log.info("Post-запрос: добавление комментария от пользователя {} к вещи {}.", userId, itemId);
        return client.addComment(userId, itemId, comment);
    }
}
