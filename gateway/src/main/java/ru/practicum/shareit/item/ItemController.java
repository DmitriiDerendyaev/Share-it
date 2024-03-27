package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final String userIDHeader  = "X-Sharer-User-Id";

    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(userIDHeader) Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Create item");
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(userIDHeader) Long userId, @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        log.info("Update item");
        return itemClient.update(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader(userIDHeader) Long userId, @PathVariable Long itemId) {
        log.info("Get information about item");
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(userIDHeader) Long userId,
                                                   @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Get all user's items");
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Object> findItems(@RequestHeader(userIDHeader) Long userId,
                                            @RequestParam(defaultValue = "Write the text") String text,
                                            @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Seach items by request with available status");
        return itemClient.findItems(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseBody
    public ResponseEntity<Object> addComment(@RequestHeader(userIDHeader) Long userId, @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        if(commentDto.getText().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return itemClient.addComment(userId, itemId, commentDto);
    }
}