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

    private final String userIDHead = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(userIDHead) long userId, @RequestBody ItemDto itemDto) {
        log.info("Create item");
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(userIDHead) long userId, @RequestBody ItemDto itemDto, @PathVariable long itemId) {
        log.info("Update item");
        return itemService.update(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader(userIDHead) long userId, @PathVariable long itemId) {
        log.info("Get information about item");
        return itemService.findById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(userIDHead) long userId) {
        log.info("Get all user's items");
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    @ResponseBody
    public List<ItemDto> findItems(@RequestHeader(userIDHead) long userId, @RequestParam(defaultValue = "Write the text") String text) {
        log.info("Seach items by request with available status");
        return itemService.findItems(text);
    }
}