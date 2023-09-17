package ru.practicum.shareit.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class MapperUtil {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static <R, E> List<R> convertList(List<E> list, Function<E, R> converter) {
        return list.stream().map(converter).collect(Collectors.toList());
    }

    public static UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public static User convertFromUserDto(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static ItemDto convertToItemDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    public static Item convertFromItemDto(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .reviews(itemDto.getReviews())
                .build();
    }
}
