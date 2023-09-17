package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Item {
    @Positive
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @Builder.Default
    private Boolean available = null;
    @Positive
    private long owner;
    @Builder.Default
    private String request = "";
    @Builder.Default
    private Set<Review> reviews = new HashSet<>();
}
