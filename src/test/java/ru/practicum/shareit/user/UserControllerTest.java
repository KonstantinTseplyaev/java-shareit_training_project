package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exceptions.ParamValidationException;
import ru.practicum.shareit.exceptions.ResponseExceptionHandler;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private UserDto userDto;
    private UserDto createdUser;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ResponseExceptionHandler.class)
                .build();
        createdUser = UserDto.builder()
                .name("John")
                .email("john.doe@mail.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john.doe@mail.com")
                .build();
    }

    @Nested
    @DisplayName("POST")
    public class MethodPost {

        @Test
        void createUser_withCorrectData() throws Exception {
            when(userService.createUser(createdUser))
                    .thenReturn(userDto);

            mvc.perform(post("/users")
                            .content(mapper.writeValueAsString(createdUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                    .andExpect(jsonPath("$.name", is(userDto.getName())))
                    .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        }

        @Test
        void createUser_withIncorrectData() throws Exception {
            when(userService.createUser(any()))
                    .thenThrow(new ParamValidationException("validation error"));

            mvc.perform(post("/users")
                            .content(mapper.writeValueAsString(userDto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(409))
                    .andExpect(jsonPath("$.error", is("Ошибка при валидации")))
                    .andExpect(jsonPath("$.errorMessage", is("validation error")));
        }
    }

    @Nested
    @DisplayName("PATCH")
    public class MethodPatch {

        @Test
        void updateUser_withCorrectData() throws Exception {
            userDto.setName("update");
            when(userService.updateUser(userDto.getId(), userDto))
                    .thenReturn(userDto);

            mvc.perform(patch("/users/{userId}", 1L)
                            .content(mapper.writeValueAsString(userDto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                    .andExpect(jsonPath("$.name", is(userDto.getName())))
                    .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        }

        @Test
        void updateUser_withIncorrectData() throws Exception {
            when(userService.updateUser(-5L, createdUser))
                    .thenThrow(new UserNotFoundException("пользователя с id -5 не существует"));

            mvc.perform(patch("/users/{userId}", -5L)
                            .content(mapper.writeValueAsString(createdUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.error", is("Ошибка при поиске пользователя")))
                    .andExpect(jsonPath("$.errorMessage", is("пользователя с id -5 не существует")));
        }
    }

    @Nested
    @DisplayName("GET")
    public class MethodGet {

        @Test
        void getAllUsers_whenUsersAreEmpty() throws Exception {
            when(userService.getAllUsers())
                    .thenReturn(new ArrayList<>());

            mvc.perform(get("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void getAllUsers_whenUsersAreNotEmpty() throws Exception {
            when(userService.getAllUsers())
                    .thenReturn(List.of(userDto));

            mvc.perform(get("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        void getUserById_withIncorrectId() throws Exception {
            when(userService.getUserById(100L))
                    .thenThrow(new UserNotFoundException("пользователя с id 100 не существует"));

            mvc.perform(get("/users/{userId}", 100L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(404))
                    .andExpect(jsonPath("$.error", is("Ошибка при поиске пользователя")))
                    .andExpect(jsonPath("$.errorMessage", is("пользователя с id 100 не существует")));
        }

        @Test
        void getUserById_withCorrectId() throws Exception {
            when(userService.getUserById(1L))
                    .thenReturn(userDto);

            mvc.perform(get("/users/{userId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                    .andExpect(jsonPath("$.name", is(userDto.getName())))
                    .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        }
    }

    @Nested
    @DisplayName("DELETE")
    public class MethodDelete {

        @Test
        void deleteAllUsers() throws Exception {
            mvc.perform(delete("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            Mockito.verify(userService, Mockito.times(1))
                    .deleteAllUsers();
        }

        @Test
        void deleteUserById() throws Exception {
            mvc.perform(delete("/users/{userId}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            Mockito.verify(userService, Mockito.times(1))
                    .deleteUserById(1L);
        }
    }
}
