package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(Long userId, Item item) {
        checkUser(userId);

        if (item.getAvailable() == null) {
            log.warn("Available can't be empty");
            throw new ValidException("Available can't be empty");
        }

        if (item.getName().isBlank()) {
            log.warn("Name can't be empty");
            throw new ValidException("Name can't be empty");
        }

        if (item.getDescription() == null) {
            log.warn("Description can't be empty");
            throw new ValidException("Description can't be empty");
        }

        if (!userRepository.containsUser(userId)) {
            log.warn("This user is not exist");
            throw new ObjectNotFoundException("This user is not exist");
        }

        return itemMapper.toItemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(Long userId, Item item, Long itemId) {
        checkUser(userId);

        if (!itemRepository.containsItem(itemId)) {
            throw new ObjectNotFoundException("This item not found");
        }

        if (itemRepository.findById(itemId).getOwner().getId() != userId) {
            throw new ObjectNotFoundException("Items can changes only owners");
        }

        Item savedItem = itemRepository.findById(itemId);
        if (item.getAvailable() != null) {
            savedItem.setAvailable(item.getAvailable());
        }

        if (item.getName() != null) {
            savedItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            savedItem.setDescription(item.getDescription());
        }

        return itemMapper.toItemDto(itemRepository.update(savedItem, itemId));
    }

    @Override
    public ItemDto findById(Long itemId) {
        if (!itemRepository.containsItem(itemId)) {
            throw new ObjectNotFoundException("Item not found");
        }
        return itemMapper.toItemDto(itemRepository.findById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        checkUser(userId);
        if (userRepository.getById(userId) == null) {
            throw new ObjectNotFoundException("User not found");
        }
        return itemRepository.getItemsByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findItems(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public void checkUser(long userId) {
        if (userId == 0) {
            log.warn("User id can't be empty");
            throw new ValidException("User id can't be empty");
        }
    }
}