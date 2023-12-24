package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    public Item create(Item item);

    public Item update(Item item, Long itemId);

    public Item findById(Long itemId);

    public List<Item> getItemsByUserId(Long userId);

    public List<Item> findItems(String text);

    public boolean containsItem(Long itemId);
}