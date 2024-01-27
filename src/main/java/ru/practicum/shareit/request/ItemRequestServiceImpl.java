package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    public ItemRequest findRequestById(Long userId, Long requestId) {
        return new ItemRequest();
    }

    @Override
    public ItemRequest addRequest(long userId, ItemRequestDto itemRequestDto) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getRequest(long userId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, Pageable pageable) {
        return null;
    }
}