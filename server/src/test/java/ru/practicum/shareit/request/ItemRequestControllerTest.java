package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService requestService;
    @InjectMocks
    private ItemRequestController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemRequestCreationDto creationRequest;
    private ItemRequestDto requestDto1;
    private ItemRequestDto requestDto2;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        creationRequest = new ItemRequestCreationDto("срочно нужен баскетбольный мяч!");

        requestDto1 = ItemRequestDto.builder()
                .id(1L)
                .description("срочно нужен баскетбольный мяч!")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        requestDto2 = ItemRequestDto.builder()
                .id(2L)
                .description("нужна скрипка на выступление")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("POST")
    public class MethodPost {

        @Test
        void createRequest() throws Exception {
            when(requestService.createRequest(1L, creationRequest))
                    .thenReturn(requestDto1);

            mvc.perform(post("/requests")
                            .header("X-Sharer-User-Id", 1L)
                            .content(mapper.writeValueAsString(creationRequest))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(requestDto1.getId()), Long.class))
                    .andExpect(jsonPath("$.description", is(requestDto1.getDescription())))
                    .andExpect(jsonPath("$.items", is(requestDto1.getItems())));
        }
    }

    @Nested
    @DisplayName("GET")
    public class MethodGet {

        @Test
        void getRequestsByUserId() throws Exception {
            when(requestService.getAllRequestsByUserId(1L))
                    .thenReturn(List.of(requestDto1, requestDto2));

            mvc.perform(get("/requests")
                            .header("X-Sharer-User-Id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void getRequestsById() throws Exception {
            when(requestService.getRequestById(anyLong(), anyLong()))
                    .thenReturn(requestDto2);

            mvc.perform(get("/requests/{requestId}", 1L)
                            .header("X-Sharer-User-Id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(requestDto2.getId()), Long.class))
                    .andExpect(jsonPath("$.description", is(requestDto2.getDescription())))
                    .andExpect(jsonPath("$.items", is(requestDto2.getItems())));
        }
    }
}
