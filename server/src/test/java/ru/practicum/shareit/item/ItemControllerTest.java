package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exceptions.ParamValidationException;
import ru.practicum.shareit.exceptions.ResponseExceptionHandler;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemCreationDto createdItem;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private CommentCreationDto creationComment;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ResponseExceptionHandler.class)
                .build();

        createdItem = ItemCreationDto.builder()
                .name("баскетбольный мяч")
                .description("хороший баскетбольный мяч")
                .available(true)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("баскетбольный мяч")
                .description("хороший баскетбольный мяч")
                .available(true)
                .owner(5L)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .itemId(1L)
                .authorName("Stan")
                .text("it's a great ball, thanks!")
                .created(LocalDateTime.now())
                .build();

        creationComment = new CommentCreationDto("it's a great ball, thanks!");
    }

    @Nested
    @DisplayName("POST")
    public class MethodPost {

        @Test
        void createItem_withCorrectData() throws Exception {
            when(itemService.createItem(createdItem, 5L))
                    .thenReturn(itemDto);

            mvc.perform(post("/items")
                            .header("X-Sharer-User-Id", 5L)
                            .content(mapper.writeValueAsString(createdItem))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                    .andExpect(jsonPath("$.name", is(itemDto.getName())))
                    .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                    .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                    .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class));
        }

        @Test
        void createItem_withIncorrectData() throws Exception {
            when(itemService.createItem(createdItem, 5L))
                    .thenThrow(new ParamValidationException("ошибка валидации"));

            mvc.perform(post("/items")
                            .header("X-Sharer-User-Id", 5L)
                            .content(mapper.writeValueAsString(createdItem))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(409))
                    .andExpect(jsonPath("$.error", is("Ошибка при валидации")))
                    .andExpect(jsonPath("$.errorMessage", is("ошибка валидации")));
        }

        @Test
        void addComment() throws Exception {
            when(itemService.addComment(1L, 1L, creationComment))
                    .thenReturn(commentDto);

            mvc.perform(post("/items/{itemId}/comment", 1L)
                            .header("X-Sharer-User-Id", 1L)
                            .content(mapper.writeValueAsString(creationComment))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                    .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Long.class))
                    .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                    .andExpect(jsonPath("$.text", is(commentDto.getText())));
        }
    }

    @Nested
    @DisplayName("PATCH")
    public class MethodPatch {

        @Test
        void updateItem() throws Exception {
            when(itemService.updateItem(any(), anyLong(), anyLong()))
                    .thenReturn(itemDto);

            mvc.perform(patch("/items/{itemId}", 1L)
                            .header("X-Sharer-User-Id", 5L)
                            .content(mapper.writeValueAsString(itemDto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                    .andExpect(jsonPath("$.name", is(itemDto.getName())))
                    .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                    .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                    .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class));
        }
    }

    @Nested
    @DisplayName("GET")
    public class MethodGet {

        @Test
        void getItemById() throws Exception {
            when(itemService.getItemById(anyLong(), anyLong()))
                    .thenReturn(itemDto);

            mvc.perform(get("/items/{itemId}", 1L)
                            .header("X-Sharer-User-Id", 5L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                    .andExpect(jsonPath("$.name", is(itemDto.getName())))
                    .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                    .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                    .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), Long.class));
        }

        @Test
        void getAllItemsByUserId() throws Exception {
            when(itemService.getAllItemsByOwnerId(anyLong(), anyInt(), anyInt()))
                    .thenReturn(List.of(itemDto, ItemDto.builder().build()));

            mvc.perform(get("/items?from=5&size=2")
                            .header("X-Sharer-User-Id", 5L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void searchItemsByText() throws Exception {
            when(itemService.searchItemsByText(anyString(), anyInt(), anyInt()))
                    .thenReturn(List.of(itemDto, ItemDto.builder().build()));

            mvc.perform(get("/items/search?from=5&size=2&text=text")
                            .header("X-Sharer-User-Id", 5L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }
}
