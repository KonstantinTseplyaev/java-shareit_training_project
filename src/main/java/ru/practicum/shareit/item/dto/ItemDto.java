package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Review;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;


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
    private long owner;
    @Builder.Default
    private String request = "";
    @Builder.Default
    private Set<Review> reviews = new HashSet<>();
}
