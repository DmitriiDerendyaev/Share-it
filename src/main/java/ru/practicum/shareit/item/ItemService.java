package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    public ItemDto create(Long userId, ItemDto itemDto);

    public ItemDto update(Long userId, ItemDto itemDto, Long itemId);

    public ItemDto findById(Long itemId);

    public List<ItemDto> getItemsByUserId(Long userId);

    public List<ItemDto> findItems(String text);
}