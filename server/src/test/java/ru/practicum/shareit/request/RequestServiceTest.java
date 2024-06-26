package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    ItemRequestMapper itemRequestMapper1 = new ItemRequestMapper();
    User user = new User(1L, "name", "mail@mail.com");
    ItemRequest itemRequest = new ItemRequest(1L, "desc", user, LocalDateTime.of(2022, Month.APRIL, 8, 12, 30));
    Item item = new Item(1L, "itemName", "itemDescription", true, user, itemRequest);

    @Test
    public void findRequestByIdTest() {
        ItemRequest itemRequest = ItemRequest.builder().build();
        Item item = Item.builder().build();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().build();
        Mockito.when(userRepository.existsById(any())).thenReturn(true);
        Mockito.when(itemRequestRepository.existsById(any())).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(1L)).thenReturn(Optional.ofNullable(itemRequest));
        assert itemRequest != null;
        Mockito.when(itemRepository.findByRequestId(itemRequest.getId())).thenReturn(List.of(item));
        List<Item> items = List.of(item);
        Mockito.when(itemRequestMapper.toDtoRequest(itemRequest, items)).thenReturn(itemRequestDto);
        ItemRequestDto result = itemRequestService.findRequestById(1L, 1L);
        Assertions.assertEquals(itemRequestDto, result);
    }

    @Test
    public void findRequestByIdUserNotExistTest() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);
        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemRequestService.findRequestById(user.getId(), itemRequest.getId()));
        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void findRequestByIdRequestNotExistTest() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRequestRepository.existsById(itemRequest.getId())).thenReturn(false);
        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemRequestService.findRequestById(user.getId(), itemRequest.getId()));
        Assertions.assertEquals("Request not found", exception.getMessage());
    }

    @Test
    public void itemRequestMapperTest() {
        ItemRequestMapper itemMapper = new ItemRequestMapper();
        User user = new User(1L, "name", "mail@mail.ru");
        User user2 = new User(2L, "name", "mail2@mail.ru");
        ItemRequest itemRequest = new ItemRequest(1L, "desc", user2, LocalDateTime.of(2023, Month.APRIL, 8, 12, 30));
        ItemDtoForRequest itemDtoForRequest = new ItemDtoForRequest(1L, "name", "desc", user2.getId(), true);
        Item item = new Item(1L, "name", "desc", true, user, itemRequest);
        ItemDtoForRequest result = itemMapper.toDtoItem(item);
        Assertions.assertEquals(itemDtoForRequest.getId(), result.getId());
    }

    @Test
    public void addRequestTest() {
        User user = new User();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        ItemRequest baseItemRequest = new ItemRequest();
        ItemRequest itemRequest = new ItemRequest();
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestMapper.toRequest(itemRequestDto, user)).thenReturn(baseItemRequest);
        Mockito.when(itemRequestRepository.save(baseItemRequest)).thenReturn(itemRequest);

        ItemRequest result = itemRequestService.addRequest(1L, itemRequestDto);

        Assertions.assertEquals(itemRequest, result);
    }

    @Test
    public void getRequestTest() {
        Item item = new Item();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        ItemRequestDto itemRequestDto1 = new ItemRequestDto();

        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(itemRequestRepository.findByRequesterId(1L)).thenReturn(List.of(itemRequest));
        List<ItemRequest> baseItemRequests = List.of(itemRequest);

        Mockito.when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        List<Item> items = List.of(item);

        Mockito.when(itemRequestMapper.toDtoRequest(itemRequest, items)).thenReturn(itemRequestDto, itemRequestDto1);

        List<ItemRequestDto> itemRequestFinal = baseItemRequests.stream().map(x -> itemRequestDto)
                .sorted(Comparator.comparing((ItemRequestDto::getCreated)))
                .collect(Collectors.toList());

        List<ItemRequestDto> result = itemRequestService.getRequest(1L);

        Assertions.assertEquals(itemRequestFinal, result);
    }

    @Test
    public void getRequestUserNotExistTest() {
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(false);
        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemRequestService.getRequest(user.getId()));
        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void getRequestEmptyTest() {
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(itemRequestRepository.findByRequesterId(user.getId())).thenReturn(Collections.emptyList());
        List<ItemRequestDto> result = itemRequestService.getRequest(user.getId());
        Assertions.assertEquals(result, Collections.emptyList());
    }

    @Test
    public void addRequestIdNotNullTest() {
        ItemDtoForRequest itemDtoForRequest = new ItemDtoForRequest();
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, LocalDateTime.now().minusDays(1), "desc", List.of(itemDtoForRequest));
        ValidException exception = Assertions.assertThrows(
                ValidException.class,
                () -> itemRequestService.addRequest(user.getId(), itemRequestDto));
        Assertions.assertEquals("id must be 0", exception.getMessage());
    }

    @Test
    public void addRequestUserNotExistTest() {
        ItemDtoForRequest itemDtoForRequest = new ItemDtoForRequest();
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, LocalDateTime.now().minusDays(1), "desc", List.of(itemDtoForRequest));
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(false);
        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemRequestService.addRequest(user.getId(), itemRequestDto));
        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void getAllRequestsTest() {
        List<ItemRequest> allItemRequest = new ArrayList<>();
        Page<ItemRequest> allPage = new PageImpl<>(allItemRequest);

        Mockito.when(itemRequestRepository.findAll(PageRequest.of(0, 1, Sort.by("created").descending())))
                .thenReturn(allPage);

        List<ItemRequestDto> outputItemRequestDto =
                itemRequestRepository.findAll(PageRequest.of(0, 1, Sort.by("created").descending())).stream()
                        .filter(x -> x.getRequester().getId() != 1L)
                        .map(x -> itemRequestMapper.toDtoRequest(x, itemRepository.findByRequestId(x.getId())))
                        .collect(Collectors.toList());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(1L, PageRequest.of(0, 1, Sort.by("created").descending()));
        Assertions.assertEquals(outputItemRequestDto, result);
    }
}