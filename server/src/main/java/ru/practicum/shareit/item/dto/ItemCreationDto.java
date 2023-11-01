package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreationDto {
    private String name;
    private String description;
    @Builder.Default
    private Boolean available = null;
    private Long requestId;
}
