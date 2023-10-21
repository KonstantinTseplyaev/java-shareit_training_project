package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private Item item1;
    private Item item2;
    private Item item3;
    private User owner;
    private User user;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;
    private ItemRequest request4;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("John")
                .email("john@example.com")
                .build();

        user = User.builder()
                .name("Peter")
                .email("peter@example.com")
                .build();

        request1 = ItemRequest.builder()
                .author(user)
                .description("нужен футбольный мяч!")
                .created(LocalDateTime.of(2023, 8, 10, 12, 0))
                .build();

        request2 = ItemRequest.builder()
                .author(user)
                .description("нужен бейсбольный мяч!")
                .created(LocalDateTime.of(2023, 8, 10, 13, 0))
                .build();

        request3 = ItemRequest.builder()
                .author(user)
                .description("нужна гитара!")
                .created(LocalDateTime.of(2023, 8, 10, 13, 1))
                .build();

        request4 = ItemRequest.builder()
                .author(user)
                .description("нужна колонка!")
                .created(LocalDateTime.of(2023, 10, 10, 12, 0))
                .build();

        item1 = Item.builder()
                .name("Баскетбольный мяч")
                .description("классный мяч")
                .available(true)
                .owner(owner)
                .build();

        item2 = Item.builder()
                .name("Футбольный мяч")
                .description("классный мяч")
                .available(true)
                .owner(owner)
                .requestId(request1.getId())
                .build();

        item3 = Item.builder()
                .name("Бейсбольный мяч")
                .description("классный мяч")
                .available(true)
                .owner(owner)
                .requestId(request2.getId())
                .build();
    }

    @Test
    void getAllRequests_forNotCreator() {
        User newOwner = userRepository.save(owner);
        User newUser = userRepository.save(user);
        item1.setOwner(newOwner);
        itemRepository.save(item1);
        item2.setOwner(newOwner);
        itemRepository.save(item2);
        item3.setOwner(newOwner);
        itemRepository.save(item3);
        request1.setAuthor(newUser);
        request2.setAuthor(newUser);
        request3.setAuthor(newUser);
        request4.setAuthor(newUser);
        ItemRequest r1 = requestRepository.save(request1);
        ItemRequest r2 = requestRepository.save(request2);
        ItemRequest r3 = requestRepository.save(request3);
        ItemRequest r4 = requestRepository.save(request4);

        List<ItemRequestDto> requestList = MapperUtil.convertList(List.of(r1, r2, r3, r4), MapperUtil::convertToItemRequestDto);
        requestList.sort(Comparator.comparing(ItemRequestDto::getCreated).reversed());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(newOwner.getId(), 0, 5);

        assertThat(requestList, equalTo(result));
    }

    @Test
    void getAllRequests_forCreator() {
        User newOwner = userRepository.save(owner);
        User newUser = userRepository.save(user);
        item1.setOwner(newOwner);
        itemRepository.save(item1);
        item2.setOwner(newOwner);
        itemRepository.save(item2);
        item3.setOwner(newOwner);
        itemRepository.save(item3);
        request1.setAuthor(newUser);
        request2.setAuthor(newUser);
        request3.setAuthor(newUser);
        request4.setAuthor(newUser);
        requestRepository.save(request1);
        requestRepository.save(request2);
        requestRepository.save(request3);
        requestRepository.save(request4);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(newUser.getId(), 0, 5);

        assertThat(result.isEmpty(), equalTo(true));
    }

    @Test
    void getAllRequestsByUserId_withResponses() {
        User newOwner = userRepository.save(owner);
        User newUser = userRepository.save(user);
        request1.setAuthor(newUser);
        request2.setAuthor(newUser);
        request3.setAuthor(newUser);
        request4.setAuthor(newUser);
        ItemRequest r1 = requestRepository.save(request1);
        ItemRequest r2 = requestRepository.save(request2);
        ItemRequest r3 = requestRepository.save(request3);
        requestRepository.save(request4);
        item1.setOwner(newOwner);
        itemRepository.save(item1);
        item2.setOwner(newOwner);
        item2.setRequestId(r1.getId());
        itemRepository.save(item2);
        item3.setOwner(newOwner);
        item3.setRequestId(r2.getId());
        itemRepository.save(item3);

        List<ItemRequestDto> result = itemRequestService.getAllRequestsByUserId(newUser.getId());

        assertThat(result.size(), equalTo(4));
        assertThat(result.get(3).getItems().isEmpty(), equalTo(false));
        assertThat(result.get(2).getItems().isEmpty(), equalTo(false));
        assertThat(result, hasItem(MapperUtil.convertToItemRequestDto(r3)));
        assertThat(result.get(0).getItems().isEmpty(), equalTo(true));
        assertThat(result.get(1).getItems().isEmpty(), equalTo(true));
    }
}
