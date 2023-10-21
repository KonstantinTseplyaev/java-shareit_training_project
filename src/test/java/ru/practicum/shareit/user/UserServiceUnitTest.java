package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    private User user1;
    private User user2;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);

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

        userDto = UserDto.builder()
                .name("John")
                .email("john@example.com")
                .build();
    }

    @Test
    void getAllUsers_whenUsersAreExist() {
        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getAllUsers_whenUsersAreEmpty() {
        when(userRepository.findAll())
                .thenReturn(new ArrayList<>());

        List<UserDto> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserById_whenUserIsExist() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));

        UserDto result = userService.getUserById(1L);

        assertEquals(user1.getId(), result.getId());
        assertEquals(user1.getName(), result.getName());
        assertEquals(user1.getEmail(), result.getEmail());
    }

    @Test
    void getUserById_whenUserIsNotExist() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L)).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("пользователя с id " + 1L + " не существует");
    }

    @Test
    void createUser_withCorrectData() {
        when(userRepository.save(MapperUtil.convertFromUserDto(userDto)))
                .thenReturn(user1);

        UserDto result = userService.createUser(userDto);

        assertEquals(user1.getId(), result.getId());
        assertEquals(user1.getName(), result.getName());
        assertEquals(user1.getEmail(), result.getEmail());
    }

    @Test
    void updateUser_withCorrectUpdateParams() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user1));
        User updatedUser = new User(1L, "john.smith@example.com", "John Smith");
        UserDto dto = UserDto.builder().name("John Smith").email("john.smith@example.com").build();
        when(userRepository.save(updatedUser))
                .thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, dto);

        assertEquals(updatedUser.getId(), result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(updatedUser.getEmail(), result.getEmail());
    }

    @Test
    void updateUser_whenUserIsNotExist() {
        when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(100L, userDto)).isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("пользователя с id " + 100L + " не существует");
    }

    @Test
    void deleteAllUsers() {
        userService.deleteAllUsers();

        verify(userRepository, times(1))
                .deleteAll();
    }

    @Test
    void deleteUserById() {
        userService.deleteUserById(1L);

        verify(userRepository, times(1))
                .deleteById(1L);
    }
}

