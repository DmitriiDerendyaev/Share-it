package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    public ItemRequest findRequestById(Long userId, Long requestId);

    public ItemRequest addRequest(long userId, ItemRequestDto itemRequestDto);

    public List<ItemRequestDto> getRequest(long userId);

    public List<ItemRequestDto> getAllRequests(long userId, Pageable pageable);

}