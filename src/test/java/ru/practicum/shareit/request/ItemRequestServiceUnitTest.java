package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exceptions.PaginationException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private ItemRequest request1;
    private User user1;
    private User user2;
    private Item item1;

    @BeforeEach
    void setUp() {
        request1 = ItemRequest.builder()
                .id(1L)
                .author(user2)
                .description("нужен баскетбольный мяч!")
                .created(LocalDateTime.now())
                .build();

        user1 = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Jane")
                .email("jane@example.com")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("Баскетбольный мяч")
                .description("классный мяч")
                .available(true)
                .owner(user1)
                .requestId(1L)
                .build();
    }

    @Test
    void createRequest_validParams_returnsCreatedRequestDto() {
        ItemRequestCreationDto creationDto = new ItemRequestCreationDto("нужен баскетбольный мяч!");

        when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(request1);

        ItemRequestDto result = itemRequestService.createRequest(user2.getId(), creationDto);

        assertEquals(request1.getId(), result.getId());
        assertEquals(request1.getCreated(), result.getCreated());
        assertEquals(request1.getDescription(), result.getDescription());
    }

    @Test
    void getAllRequestsByUserId() {
        List<ItemRequest> requestList = List.of(request1);

        ItemForRequestDto itemForRequestDto = MapperUtil.convertToItemForRequestDto(item1);

        Set<Long> requestsId = requestList.stream().map(ItemRequest::getId).collect(Collectors.toSet());

        List<Item> itemList = List.of(item1);

        List<ItemForRequestDto> items = List.of(itemForRequestDto);

        Map<Long, List<ItemForRequestDto>> itemsMap = items.stream()
                .collect(Collectors.groupingBy(ItemForRequestDto::getRequestId));

        when(userRepository.existsById(user2.getId()))
                .thenReturn(true);
        when(requestRepository.findByAuthorIdOrderByCreatedDesc(user2.getId()))
                .thenReturn(requestList);
        when(itemRepository.findAllByRequestIdIn(requestsId))
                .thenReturn(itemList);

        List<ItemRequestDto> result = itemRequestService.getAllRequestsByUserId(user2.getId());

        assertEquals(1, result.size());
        assertEquals(request1.getId(), result.get(0).getId());
        assertEquals(itemsMap.get(request1.getId()), result.get(0).getItems());
    }

    @Test
    void getAllRequestsByUserId_withIncorrectId() {
        when(userRepository.existsById(user2.getId()))
                .thenReturn(false);

        assertThatThrownBy(() -> itemRequestService.getAllRequestsByUserId(user2.getId())).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("юзера с id " + user2.getId() + " нет");
    }

    @Test
    void getAllRequests() {
        ItemRequest request3 = ItemRequest.builder()
                .id(3L)
                .author(user2)
                .description("нужен футбольный мяч!")
                .created(LocalDateTime.now())
                .build();

        ItemForRequestDto itemForRequestDto = MapperUtil.convertToItemForRequestDto(item1);

        List<ItemForRequestDto> items = List.of(itemForRequestDto);

        List<ItemRequest> requestList = List.of(request1, request3);

        Set<Long> requestsId = requestList.stream().map(ItemRequest::getId).collect(Collectors.toSet());

        List<Item> itemsList = List.of(item1);

        Map<Long, List<ItemForRequestDto>> itemsMap = items.stream()
                .collect(Collectors.groupingBy(ItemForRequestDto::getRequestId));

        when(userRepository.existsById(user1.getId()))
                .thenReturn(true);
        when(requestRepository.findByAuthorIdIsNot(user1.getId(), PageRequest.of(0, 10,
                Sort.by(Sort.Direction.DESC, "created"))))
                .thenReturn(requestList);
        when(itemRepository.findAllByRequestIdIn(requestsId))
                .thenReturn(itemsList);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(user1.getId(), 0, 10);

        assertEquals(2, result.size());
        assertEquals(request1.getId(), result.get(0).getId());
        assertEquals(request3.getId(), result.get(1).getId());
        assertEquals(itemsMap.get(request1.getId()), result.get(0).getItems());
    }

    @Test
    void getAllRequests_withIncorrectPaginationParams() {
        when(userRepository.existsById(user2.getId()))
                .thenReturn(true);

        assertThatThrownBy(() -> itemRequestService.getAllRequests(user2.getId(), -1, 2)).isInstanceOf(PaginationException.class)
                .hasMessageContaining("неверные параметры пагинации");
    }

    @Test
    void getRequestById() {
        List<Item> items = List.of(item1);
        when(userRepository.existsById(user2.getId()))
                .thenReturn(true);
        when(requestRepository.findById(request1.getId()))
                .thenReturn(Optional.of(request1));
        when(itemRepository.findByRequestId(request1.getId()))
                .thenReturn(items);

        ItemRequestDto result = itemRequestService.getRequestById(user2.getId(), request1.getId());

        assertEquals(request1.getId(), result.getId());
        assertEquals((MapperUtil.convertToItemForRequestDto(item1)), result.getItems().get(0));
    }
}
