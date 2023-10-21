package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.mapper.MapperUtil;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return MapperUtil.convertList(users, MapperUtil::convertToUserDto);
    }

    @Override
    public UserDto getUserById(Long id) {
        return MapperUtil.convertToUserDto(getUser(id));
    }

    @Override
    public User getUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElseThrow(() ->
                new UserNotFoundException("пользователя с id " + id + " не существует"));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User newUser = MapperUtil.convertFromUserDto(userDto);
        return MapperUtil.convertToUserDto(userRepository.save(newUser));

    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = updateUserFromDtoParam(userId, userDto);
        User updatedUser = userRepository.save(user);
        return MapperUtil.convertToUserDto(updatedUser);
    }

    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private User updateUserFromDtoParam(Long userId, UserDto userDto) {
        Optional<User> updatedUserOp = userRepository.findById(userId);
        User updatedUser = updatedUserOp.orElseThrow(() ->
                new UserNotFoundException("пользователя с id " + userId + " не существует"));
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }
        return updatedUser;
    }
}
