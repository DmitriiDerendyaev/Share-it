package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwners;

import java.util.List;

public interface ItemService {
    public ItemDto create(Long userId, ItemDto itemDto);

    public ItemDto update(Long userId, ItemDto itemDto, Long itemId);

    ItemDtoForOwners findById(Long itemId, Long userId);

    public List<ItemDtoForOwners> getItemsByUserId(Long userId);

    public List<ItemDto> findItems(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}