package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemForBookingDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController controller;
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    private MockMvc mvc;
    private BookingCreationDto creationBooking;
    private BookingDto bookingDto1;
    private BookingDto bookingDto2;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        creationBooking = BookingCreationDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 12, 1, 23, 30))
                .end(LocalDateTime.of(2023, 12, 2, 16, 5))
                .build();

        bookingDto1 = BookingDto.builder()
                .id(1L)
                .item(new ItemForBookingDto(1L, "name1", "description1"))
                .booker(new UserDto(1L, "john.doe@mail.com", "John"))
                .start(LocalDateTime.of(2023, 12, 1, 23, 30))
                .end(LocalDateTime.of(2023, 12, 2, 16, 5))
                .status(State.WAITING)
                .build();

        bookingDto2 = BookingDto.builder()
                .id(2L)
                .item(new ItemForBookingDto(2L, "name2", "description2"))
                .booker(new UserDto(2L, "karl.doe@mail.com", "Karl"))
                .start(LocalDateTime.of(2023, 11, 7, 12, 55))
                .end(LocalDateTime.of(2023, 11, 14, 16, 25))
                .status(State.WAITING)
                .build();
    }

    @Nested
    @DisplayName("POST")
    public class MethodPost {

        @Test
        void createBooking() throws Exception {
            when(bookingService.createBooking(5L, creationBooking))
                    .thenReturn(bookingDto1);

            mvc.perform(post("/bookings")
                            .header("X-Sharer-User-Id", 5L)
                            .content(mapper.writeValueAsString(creationBooking))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andDo(print())
                    .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                    .andExpect(jsonPath("$.item.id", is(bookingDto1.getItem().getId()), Long.class))
                    .andExpect(jsonPath("$.item.name", is(bookingDto1.getItem().getName())))
                    .andExpect(jsonPath("$.item.description", is(bookingDto1.getItem().getDescription())))
                    .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId()), Long.class))
                    .andExpect(jsonPath("$.booker.email", is(bookingDto1.getBooker().getEmail())))
                    .andExpect(jsonPath("$.booker.name", is(bookingDto1.getBooker().getName())))
                    .andExpect(jsonPath("$.start", is(List.of(2023, 12, 1, 23, 30))))
                    .andExpect(jsonPath("$.end", is(List.of(2023, 12, 2, 16, 5))))
                    .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().name())));
        }
    }

    @Nested
    @DisplayName("PATCH")
    public class MethodPatch {

        @Test
        void confirmationBooking() throws Exception {
            when(bookingService.confirmationBooking(anyLong(), anyLong(), anyBoolean()))
                    .thenReturn(bookingDto1);

            mvc.perform(patch("/bookings/{bookingId}?approved=true", 1L)
                            .header("X-Sharer-User-Id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andDo(print())
                    .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                    .andExpect(jsonPath("$.item.id", is(bookingDto1.getItem().getId()), Long.class))
                    .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId()), Long.class))
                    .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().name())));
        }
    }

    @Nested
    @DisplayName("GET")
    public class MethodGet {

        @Test
        void getBookingById() throws Exception {
            when(bookingService.getBookingById(anyLong(), anyLong()))
                    .thenReturn(bookingDto1);

            mvc.perform(get("/bookings/{bookingId}", 1L)
                            .header("X-Sharer-User-Id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andDo(print())
                    .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
                    .andExpect(jsonPath("$.item.id", is(bookingDto1.getItem().getId()), Long.class))
                    .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId()), Long.class))
                    .andExpect(jsonPath("$.status", is(bookingDto1.getStatus().name())));
        }

        @Test
        void getAllBookingsByUserId() throws Exception {
            when(bookingService.getAllBookingsByUserId(anyLong(), anyString(), anyInt(), anyInt()))
                    .thenReturn(List.of(bookingDto1, bookingDto2));

            mvc.perform(get("/bookings?state=All&from=10&size=3")
                            .header("X-Sharer-User-Id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andDo(print())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void getAllBookingsByOwnerId() throws Exception {
            when(bookingService.getAllBookingsByUserId(anyLong(), anyString(), anyInt(), anyInt()))
                    .thenReturn(List.of(bookingDto1, bookingDto2));

            mvc.perform(get("/bookings?state=All&from=10&size=3")
                            .header("X-Sharer-User-Id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()).andDo(print())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }
}
