package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingCreationDto {
    @NotNull
    private final Long itemId;
    @FutureOrPresent
    @NotNull
    private final LocalDateTime start;
    @Future
    @NotNull
    private final LocalDateTime end;
}
