package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    public ItemRequest findRequestById(Long userId, Long requestId) {
        return new ItemRequest();
    }
}