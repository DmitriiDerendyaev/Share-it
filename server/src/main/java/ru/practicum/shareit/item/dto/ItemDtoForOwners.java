package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoForOwners {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long request;

    private BookingOwnerDto lastBooking;

    private BookingOwnerDto nextBooking;

    private List<CommentDto> comments;
}