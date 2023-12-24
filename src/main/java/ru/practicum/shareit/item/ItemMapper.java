package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    private final UserService userService;

    private final ItemRequestService itemRequestService;

    private final UserMapper userMapper;

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .request(item.getRequest() != null ? item.getRequest().getId() : 0)
                .build();
    }

    public Item toItem(ItemDto itemDto, long userId) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request(itemDto.getRequest() != null ? itemRequestService.findRequestById(itemDto.getRequest(), userId) : null)
                .owner(userService.getById(userId) != null ? userMapper.toUser(userService.getById(userId)) : null)
                .build();
    }
}