package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.DELEGATING)})
@NoArgsConstructor
public class RequestCreationDto {
    @NotBlank()
    @Length(max = 500)
    @JsonProperty("description")
    private String description;
}
