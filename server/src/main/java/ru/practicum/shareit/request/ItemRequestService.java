package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    public ItemRequest findRequestByIdUtil(Long userId, Long requestId);

    public ItemRequestDto findRequestById(Long userId, Long requestId);

    public ItemRequest addRequest(Long userId, ItemRequestDto itemRequestDto);

    public List<ItemRequestDto> getRequest(Long userId);

    public List<ItemRequestDto> getAllRequests(Long userId, Pageable pageable);

}