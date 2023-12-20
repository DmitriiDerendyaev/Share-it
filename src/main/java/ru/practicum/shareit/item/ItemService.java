package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    public ItemDto create(Long userId, Item item);

    public ItemDto update(Long userId, Item item, Long itemId);

    public ItemDto findById(Long itemId);

    public List<ItemDto> getItemsByUserId(Long userId);

    public List<ItemDto> findItems(String text);
}