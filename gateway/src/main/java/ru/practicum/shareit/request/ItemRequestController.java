package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final String userIDHeader  = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @GetMapping()
    public ResponseEntity<Object> getRequest(@RequestHeader(userIDHeader) Long userId) {

        return itemRequestClient.getRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(userIDHeader) Long userId,
                                               @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestById(@RequestHeader(userIDHeader) Long userId, @PathVariable Long requestId) {
        return itemRequestClient.findRequestById(userId, requestId);
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(userIDHeader) Long userId, @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }
}