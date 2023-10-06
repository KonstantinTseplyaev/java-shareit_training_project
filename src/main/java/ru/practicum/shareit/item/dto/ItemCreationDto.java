package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreationDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Builder.Default
    private Boolean available = null;
}
