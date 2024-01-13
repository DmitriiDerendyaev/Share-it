package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto2;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemDtoForOwners {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private long request;

    private BookingDto2 lastBooking;

    private BookingDto2 nextBooking;

    private List<CommentDto> comments;
}