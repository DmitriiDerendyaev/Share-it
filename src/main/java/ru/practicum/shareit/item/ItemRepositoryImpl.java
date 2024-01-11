package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    private long itemId;

    @Override
    public Item create(Item item) {
        item.setId(incrementId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, Long itemId) {
        items.put(itemId, item);
        return item;
    }

    @Override
    public Item findById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        return items.values().stream()
                .filter(x -> Objects.equals(x.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItems(String text) {
        return items.values().stream()
                .filter(x -> x.getName().toLowerCase().contains(text.toLowerCase()) || x.getDescription().toLowerCase().contains(text.toLowerCase()) && (x.getAvailable()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsItem(Long itemId) {
        return items.containsKey(itemId);
    }

    private long incrementId() {
        return ++itemId;
    }
}