package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestMapper itemRequestMapper;


    public ItemRequest findRequestByIdUtil(Long userId, Long requestId) {
        return new ItemRequest();
    }

    public ItemRequestDto findRequestById(Long userId, Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User not found");
        }
        if (!itemRequestRepository.existsById(requestId)) {
            throw new ObjectNotFoundException("Request not found");
        }
        ItemRequest baseItemRequest = itemRequestRepository.findById(requestId).orElseThrow();
        return itemRequestMapper.toDtoRequest(baseItemRequest, itemRepository.findByRequestId(baseItemRequest.getId()));
    }

    @Override
    public ItemRequest addRequest(Long userId, ItemRequestDto itemRequestDto) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getRequest(Long userId) {
        return null;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Pageable pageable) {
        return null;
    }


}