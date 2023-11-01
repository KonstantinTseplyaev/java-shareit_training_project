package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.mapper.MapperUtil.USER_ID_HEADER;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(USER_ID_HEADER) long userId,
                                        @RequestBody ItemRequestCreationDto requestCreationDto) {
        log.info("Post-запрос: добавление нового запроса вещи от пользователя {}: {}.", userId, requestCreationDto);
        return requestService.createRequest(userId, requestCreationDto);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Get-запрос: получение списка запросов юзера {}", userId);
        return requestService.getAllRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) long userId,
                                               @RequestParam int from,
                                               @RequestParam int size) {
        log.info("Get-запрос: получение постраничного списка всех существующих запросов для юзера {}", userId);
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_HEADER) long userId,
                                         @PathVariable long requestId) {
        log.info("Get-запрос: получение запроса с id {} юзером {}", requestId, userId);
        return requestService.getRequestById(userId, requestId);
    }
}
