package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.DELEGATING)})
@NoArgsConstructor
public class CommentCreationDto {
    @JsonProperty("text")
    private String text;
}
