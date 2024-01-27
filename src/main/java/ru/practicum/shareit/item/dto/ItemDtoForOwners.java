package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;
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

    private BookingOwnerDto lastBooking;

    private BookingOwnerDto nextBooking;

    private List<CommentDto> comments;
}