package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Builder.Default
    private Boolean available = null;
    private Long owner;
    private Long requestId;
    @Setter
    @Getter
    private List<CommentDto> comments;
    @Setter
    private BookingForItemDto lastBooking;
    @Setter
    private BookingForItemDto nextBooking;
}
