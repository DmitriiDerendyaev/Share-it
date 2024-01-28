package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final ItemRequestMapper itemRequestMapper;

    @Deprecated
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
        if (itemRequestDto.getId() != 0) {
            log.warn("id must be 0");
            throw new ValidException("id must be 0");
        }

        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User not found");
        }

        User user = userRepository.findById(userId).orElseThrow();
        ItemRequest baseItemRequest = itemRequestMapper.toRequest(itemRequestDto, user);

        return itemRequestRepository.save(baseItemRequest);
    }

    @Override
    public List<ItemRequestDto> getRequest(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User not found");
        }
        List<ItemRequest> baseItemRequests = itemRequestRepository.findByRequesterId(userId);
        if (baseItemRequests.isEmpty()) {
            return Collections.emptyList();
        }

        return baseItemRequests.stream().map(x -> itemRequestMapper.toDtoRequest(x, itemRepository.findByRequestId(x.getId())))
                .sorted(Comparator.comparing(ItemRequestDto::getCreated)).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Pageable pageable) {
        return itemRequestRepository.findAll(pageable).stream()
                .filter(x -> !Objects.equals(x.getRequester().getId(), userId))
                .map(x -> itemRequestMapper.toDtoRequest(x, itemRepository.findByRequestId(x.getId())))
                .collect(Collectors.toList());
    }


}