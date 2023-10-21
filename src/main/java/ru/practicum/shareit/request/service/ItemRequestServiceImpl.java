package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.PaginationException;
import ru.practicum.shareit.exceptions.RequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(long userId, ItemRequestCreationDto requestCreationDto) {
        Optional<User> userOpt = userRepository.findById(userId);
        User user = userOpt.orElseThrow(() -> new UserNotFoundException("юзера с id " + userId + " не существует"));
        ItemRequest itemRequest = MapperUtil.convertFromItemRequestCreationDto(requestCreationDto, user);
        return MapperUtil.convertToItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByUserId(long userId) {
        checkUser(userId);
        List<ItemRequest> requestList = requestRepository.findByAuthorIdOrderByCreatedDesc(userId);
        Set<Long> requestsId = requestList.stream().map(ItemRequest::getId).collect(Collectors.toSet());
        Map<Long, List<ItemForRequestDto>> items = MapperUtil
                .convertList(itemRepository.findAllByRequestIdIn(requestsId), MapperUtil::convertToItemForRequestDto)
                .stream().collect(Collectors.groupingBy(ItemForRequestDto::getRequestId));
        return requestList.stream().map(request ->
                MapperUtil.convertToItemRequestDto(request, items.getOrDefault(request.getId(),
                        new ArrayList<>()))).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        checkUser(userId);
        if (from < 0 || size < 1) throw new PaginationException("неверные параметры пагинации");
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requests = requestRepository.findByAuthorIdIsNot(userId, pageable);
        Set<Long> requestsId = requests.stream().map(ItemRequest::getId).collect(Collectors.toSet());
        Map<Long, List<ItemForRequestDto>> items = MapperUtil
                .convertList(itemRepository.findAllByRequestIdIn(requestsId), MapperUtil::convertToItemForRequestDto)
                .stream().collect(Collectors.groupingBy(ItemForRequestDto::getRequestId));
        return requests.stream().map(request ->
                MapperUtil.convertToItemRequestDto(request, items.getOrDefault(request.getId(),
                        new ArrayList<>()))).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        checkUser(userId);
        Optional<ItemRequest> requestOpt = requestRepository.findById(requestId);
        ItemRequest request = requestOpt.orElseThrow(() ->
                new RequestNotFoundException("запроса с id " + requestId + " не существует"));
        List<Item> responses = itemRepository.findByRequestId(requestId);
        if (responses.isEmpty()) return MapperUtil.convertToItemRequestDto(request);
        else return MapperUtil.convertToItemRequestDto(request, MapperUtil
                .convertList(responses, MapperUtil::convertToItemForRequestDto));
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId)) throw new UserNotFoundException("юзера с id " + userId + " нет");
    }
}
