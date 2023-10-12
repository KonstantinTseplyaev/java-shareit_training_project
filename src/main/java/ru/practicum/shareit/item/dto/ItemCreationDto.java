package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class ItemCreationDto {
    @NotBlank
    private final String name;
    @NotBlank
    private final String description;
    @Builder.Default
    private Boolean available = null;
}
