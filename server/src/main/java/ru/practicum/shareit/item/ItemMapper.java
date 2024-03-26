package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comments;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwners;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Builder
public class ItemMapper {

    private final UserMapper userMapper;

    public ItemMapper() {

        userMapper = new UserMapper();
    }

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : 0)
                .build();
    }

    public Item toItem(ItemDto itemDto, ItemRequest itemRequest, UserDto userDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequestId() != null ? itemRequest : null)
                .owner(userDto != null ? userMapper.toUser(userDto) : null)
                .build();
    }

    public ItemDtoForOwners toItemDtoForOwners(Item item, Long userId, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {

        return ItemDtoForOwners.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : 0)
                .lastBooking((lastBooking != null) && (item.getOwner().getId() == userId) ?
                        BookingOwnerDto.builder()
                                .id(lastBooking.getId())
                                .start(lastBooking.getStart())
                                .end(lastBooking.getEnd())
                                .bookerId(lastBooking.getBooker().getId())
                                .build() : null
                )

                .nextBooking((nextBooking != null) && (item.getOwner().getId() == userId) ?
                        BookingOwnerDto.builder()
                                .id(nextBooking.getId())
                                .start(nextBooking.getStart())
                                .end(nextBooking.getEnd())
                                .bookerId(nextBooking.getBooker().getId())
                                .build() : null
                )
                .comments(comments)
                .build();
    }

    public CommentDto toCommentDto(Comments comments) {
        return CommentDto.builder()
                .id(comments.getId())
                .author(comments.getAuthor().getId())
                .authorName(comments.getAuthor().getName())
                .text(comments.getText())
                .created(comments.getCreated())
                .build();
    }

    public Comments toComment(CommentDto commentDto, User user, Item item) {
        Comments comments = new Comments();
        comments.setId(commentDto.getId());
        comments.setText(commentDto.getText());
        comments.setItem(item);
        comments.setAuthor(user);
        comments.setCreated(LocalDateTime.now());
        return comments;
    }
}