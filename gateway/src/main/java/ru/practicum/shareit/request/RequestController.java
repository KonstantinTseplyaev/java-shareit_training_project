package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.RequestCreationDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.client.BaseClient.USER_ID_HEADER;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient client;

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @PathVariable long requestId) {
        log.info("Get-запрос: получение запроса с id {} юзером {}", requestId, userId);
        return client.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "20") @Positive int size) {
        log.info("Get-запрос: получение постраничного списка всех существующих запросов для юзера {}", userId);
        return client.getAllRequests(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get-запрос: получение списка запросов юзера {}", userId);
        return client.getRequestsByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                                @RequestBody @Valid RequestCreationDto requestCreationDto) {
        log.info("Post-запрос: добавление нового запроса вещи от пользователя {}: {}.", userId, requestCreationDto);
        return client.createRequest(userId, requestCreationDto);
    }
}
