package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final String userIDhead = "X-Sharer-User-Id";

    private final ItemService itemService;

    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto create(@RequestHeader(userIDhead) long userId, @RequestBody ItemDto itemDto) {
        log.info("Create item");
        return itemService.create(userId, itemMapper.toItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(userIDhead) long userId, @RequestBody ItemDto itemDto, @PathVariable long itemId) {
        log.info("Update item");
        return itemService.update(userId, itemMapper.toItem(itemDto, userId), itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(userIDhead) long userId, @PathVariable long itemId) {
        log.info("Get information about item");
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(userIDhead) long userId) {
        log.info("Get all user's items");
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseBody
    public List<ItemDto> findItems(@RequestHeader(userIDhead) long userId, @RequestParam(defaultValue = "Write the text") String text) {
        log.info("Seach items by request with available status");
        return itemService.findItems(text);
    }
}