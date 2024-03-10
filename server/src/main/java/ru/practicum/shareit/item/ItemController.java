package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwners;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final String userIDHeader  = "X-Sharer-User-Id";

    private final ItemService itemService;

    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto create(@RequestHeader(userIDHeader) Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Create item");
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(userIDHeader) Long userId, @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        log.info("Update item");
        return itemService.update(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoForOwners findById(@RequestHeader(userIDHeader) Long userId, @PathVariable Long itemId) {
        log.info("Get information about item");
        return itemService.findById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoForOwners> getItemsByUserId(@RequestHeader(userIDHeader) Long userId,
                                                   @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Get all user's items");
        return itemService.getItemsByUserId(userId, PageRequest.of(from / size, size, Sort.by("id")));
    }

    @GetMapping("/search")
    @ResponseBody
    public List<ItemDto> findItems(@RequestHeader(userIDHeader) Long userId,
                                   @RequestParam(defaultValue = "Write the text") String text,
                                   @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                   @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Seach items by request with available status");
        return itemService.findItems(text, PageRequest.of(from / size, size));
    }

    @PostMapping("/{itemId}/comment")
    @ResponseBody
    public CommentDto addComment(@RequestHeader(userIDHeader) Long userId, @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}