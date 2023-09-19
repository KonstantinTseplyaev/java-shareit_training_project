package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationUserException;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private long currentId = 0;

    private final UserRepository userRepository;

    private final Map<Long, String> emails = new HashMap<>();

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.getAll();
        return MapperUtil.convertList(users, MapperUtil::convertToUserDto);
    }

    @Override
    public UserDto getUserById(Long id) {
        return MapperUtil.convertToUserDto(userRepository.getById(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = MapperUtil.convertFromUserDto(userDto);
        checkEmail(newUser.getEmail());
        newUser.setId(++currentId);
        emails.put(newUser.getId(), newUser.getEmail());
        return MapperUtil.convertToUserDto(userRepository.create(newUser));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = updateUserFromDtoParam(userId, userDto);
        User updatedUser = userRepository.update(user);
        emails.put(updatedUser.getId(), updatedUser.getEmail());
        return MapperUtil.convertToUserDto(updatedUser);
    }

    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
        emails.clear();
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
        emails.remove(id);
    }

    private void checkEmail(String email) {
        if (emails.containsValue(email))
            throw new ValidationUserException("email " + email + " уже существует");
    }

    private User updateUserFromDtoParam(Long userId, UserDto userDto) {
        User updatedUser = userRepository.getById(userId);
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (!userDto.getEmail().equals(emails.get(userId))) {
                checkEmail(userDto.getEmail());
            }
            @Valid String email = userDto.getEmail();
            updatedUser.setEmail(email);
        }
        return updatedUser;
    }
}
