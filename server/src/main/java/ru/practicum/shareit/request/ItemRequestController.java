package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final String userIDHeader  = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @GetMapping()
    public List<ItemRequestDto> getRequest(@RequestHeader(userIDHeader) Long userId) {

        return itemRequestService.getRequest(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(userIDHeader) Long userId,
                                               @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        return itemRequestService.getAllRequests(userId, PageRequest.of(from / size, size, Sort.by("created").descending()));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findRequestById(@RequestHeader(userIDHeader) Long userId, @PathVariable Long requestId) {
        return itemRequestService.findRequestById(userId, requestId);
    }

    @PostMapping
    public ItemRequest addRequest(@RequestHeader(userIDHeader) Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(userId, itemRequestDto);
    }
}