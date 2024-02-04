package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comments;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwners;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestService itemRequestService;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Test
    public void createItemTest() {
        Long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(null)
                .build();

        UserDto userDto = UserDto.builder()
                .id(userId)
                .name("Test User")
                .email("test@example.com")
                .build();

        User user = User.builder()
                .id(userId)
                .name("Test User")
                .email("test@example.com")
                .build();

        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .request(null)
                .owner(user)
                .build();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.toDto(user)).thenReturn(userDto);
        Mockito.when(itemMapper.toItem(itemDto, null, userDto)).thenReturn(item);
        Mockito.when(itemMapper.toItemDto(Mockito.any())).thenReturn(itemDto);
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);

        ItemDto createdItemDto = itemService.create(userId, itemDto);

        Assertions.assertEquals(itemDto, createdItemDto);
    }

    User user = User.builder()
            .id(1L)
            .name("Николай")
            .email("nik@mail.ru")
            .build();

    UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Test User")
            .email("test@example.com")
            .build();


    Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description item")
            .available(true)
            .owner(user)
            .request(null)
            .build();

    Item itemExtra = Item.builder()
            .id(1L)
            .name("item another")
            .description("description item another")
            .available(true)
            .owner(user)
            .request(null)
            .build();

    ItemDto itemDtoMapper = ItemDto.builder()
            .id(1L)
            .name("item")
            .description("description item")
            .available(true)
            .requestId(0L)
            .build();

    ItemMapper itemMapper1 = new ItemMapper();
    ItemDto itemDto2 = ItemDto.builder()
            .id(0L)
            .name("item")
            .description("description item")
            .available(true)
            .requestId(1L)
            .build();

    ItemDto itemDtoInput = ItemDto.builder()
            .id(0L)
            .name("item")
            .description("description item")
            .available(true)
            .requestId(1L)
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("Нико")
            .email("nik7@mail.ru")
            .build();

    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .created(LocalDateTime.now())
            .requester(user)
            .description("some text")
            .build();

    @Test
    public void createWithNotNullIdTest() {
        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.create(user.getId(), itemDto2));
        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void createWithNotExistUserTest() {
        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.create(user.getId(), itemDtoInput));
        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void userUpdateTest() {
        when(itemRequestService.findRequestByIdUtil(itemDtoInput.getRequestId(), 1L)).thenReturn(itemRequest);
        when(userService.getById(1L)).thenReturn(userDto);
        when(itemMapper.toItem(itemDtoInput, itemRequest, userDto)).thenReturn(item);
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.ofNullable(itemExtra));

        when(itemRepository.save(any(Item.class))).thenReturn(itemExtra);
        when(itemMapper.toItemDto(itemExtra)).thenReturn(itemDto2);

        ItemDto result = itemService.update(1L, itemDto2, 1L);

        Assertions.assertEquals(itemDto2.getId(), result.getId());
        Assertions.assertEquals(itemDto2.getName(), result.getName());
        Assertions.assertEquals(itemDto2.getDescription(), result.getDescription());
    }


    @Test
    public void updateItemNotExistTest() {
        Mockito.when(itemRepository.existsById(item.getId())).thenReturn(false);
        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.update(user.getId(), itemDtoInput, item.getId()));
        Assertions.assertEquals("This item not found", exception.getMessage());
    }

    @Test
    public void userIdCanNotBeEmptyTest() {
        ValidException exception = Assertions.assertThrows(
                ValidException.class,
                () -> itemService.update(0L, itemDtoInput, item.getId()));
        Assertions.assertEquals("User id can't be empty", exception.getMessage());
    }

    @Test
    public void findByIdItemNotExistTest() {
        Mockito.when(itemRepository.existsById(item.getId())).thenReturn(false);
        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.findById(user.getId(), item.getId()));
        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    public void findByIdTest() {
        List<Booking> bookings = new ArrayList<>();
        when(itemRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByItemIdAndStatus(1L, BookingStatus.APPROVED)).thenReturn(bookings);

        List<Comments> comments = new ArrayList<>();
        when(commentRepository.findByItemId(1L)).thenReturn(comments);

        List<CommentDto> commentsDto = new ArrayList<>();

        ItemDtoForOwners itemDtoForOwners = ItemDtoForOwners.builder()
                .id(1L)
                .name("item")
                .description("description item")
                .available(true)
                .request(0)
                .lastBooking(null)
                .nextBooking(null)
                .comments(commentsDto)
                .build();

        when(itemRepository.getReferenceById(1L)).thenReturn(item);
        when(itemMapper.toItemDtoForOwners(item, 1L, null, null, commentsDto)).thenReturn(itemDtoForOwners);

        ItemDtoForOwners result = itemService.findById(1L, 1L);

        Assertions.assertEquals(itemDtoForOwners, result);
    }

    @Test
    public void getItemsByUserIdTest() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.existsById(1L)).thenReturn(true);
        List<Item> items = List.of(item);
        Mockito.when(itemRepository.findByOwnerId(1L)).thenReturn(items);
        List<ItemDtoForOwners> outputItems = items.stream()
                .map(x -> itemService.findById(x.getId(), user.getId()))
                .collect(Collectors.toList());

        List<ItemDtoForOwners> result = itemService.getItemsByUserId(1L);
        Assertions.assertEquals(outputItems, result);
    }

    @Test
    public void getItemsByUserIdNotExistTest() {
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);
        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.getItemsByUserId(user.getId()));
        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void findItemsTest() {
        String text = "tekst";
        List<Item> items = List.of(item);
        Mockito.when(itemRepository.search(text)).thenReturn(items);
        List<ItemDto> itemsResult = items.stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        List<ItemDto> result = itemService.findItems(text);
        Assertions.assertEquals(itemsResult, result);
    }


    @Test
    public void findItemsTextIsBlankTest() {
        List<ItemDto> items = itemService.findItems("");
        Assertions.assertEquals(items, Collections.emptyList());
    }

    @Test
    public void addCommentTest() {
        User user2 = User.builder()
                .id(2L)
                .name("Иван")
                .email("nik1@mail.ru")
                .build();

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2023, Month.APRIL, 8, 12, 30));
        booking.setEnd(LocalDateTime.of(2023, Month.APRIL, 10, 12, 30));
        booking.setItem(item);
        booking.setBooker(user2);
        booking.setStatus(BookingStatus.APPROVED);

        CommentDto commentDto = CommentDto.builder()
                .text("comment1")
                .build();

        Comments comments1 = Comments.builder()
                .id(1L)
                .author(user2)
                .text("comment1")
                .item(item)
                .created(LocalDateTime.now())
                .build();

        Mockito.when(bookingRepository.findByBookerIdAndItemId(2L, 1L)).thenReturn(List.of(booking));

        Mockito.when(userRepository.getReferenceById(2L)).thenReturn(user);
        Mockito.when(itemRepository.getReferenceById(1L)).thenReturn(item);

        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comments1);

        CommentDto newComment = itemMapper1.toCommentDto(comments1);
        Mockito.when(itemMapper.toCommentDto(comments1)).thenReturn(newComment);

        CommentDto result = itemService.addComment(2L, 1L, commentDto);
        Assertions.assertEquals(commentDto.getText(), result.getText());
    }

    @Test
    public void addCommentTextIsBlankTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("");
        ValidException exception = Assertions.assertThrows(
                ValidException.class,
                () -> itemService.addComment(user.getId(), item.getId(), commentDto));
        Assertions.assertEquals("This field can't be empty, write the text", exception.getMessage());
    }

    @Test
    public void addCommentBookingsItemByUserIsEmptyTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("xfdbxdgn");
        Mockito.when(bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId())).thenReturn(Collections.emptyList());
        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemService.addComment(user.getId(), item.getId(), commentDto));
        Assertions.assertEquals("You can't write the comment, because you didn't booking this item", exception.getMessage());
    }

    @Test
    public void addCommentBookingsEndsBeforeNowTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("xfdbxdgn");
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), item, user, BookingStatus.APPROVED);
        Mockito.when(bookingRepository.findByBookerIdAndItemId(user.getId(), item.getId())).thenReturn(List.of(booking));
        ValidException exception = Assertions.assertThrows(
                ValidException.class,
                () -> itemService.addComment(user.getId(), item.getId(), commentDto));
        Assertions.assertEquals("You can't comment, because you didn't use this item", exception.getMessage());
    }

    @Test
    public void itemMapperToItemDtoTest() {
        ItemMapper itemMapper1 = new ItemMapper();
        ItemDto result = itemMapper1.toItemDto(item);
        Assertions.assertEquals(result.getId(), itemDtoMapper.getId());
        Assertions.assertEquals(result.getName(), itemDtoMapper.getName());
        Assertions.assertEquals(result.getDescription(), itemDtoMapper.getDescription());
        Assertions.assertEquals(result.getAvailable(), itemDtoMapper.getAvailable());
        Assertions.assertEquals(result.getRequestId(), itemDtoMapper.getRequestId());
    }

    @Test
    public void toCommentDtoTest() {
        ItemMapper itemMapper1 = new ItemMapper();
        Comments comments = new Comments(1L, "comment1", item, user, LocalDateTime.now().minusDays(1));
        CommentDto result = itemMapper1.toCommentDto(comments);
        Assertions.assertEquals(result.getId(), comments.getId());
        Assertions.assertEquals(result.getAuthor(), comments.getAuthor().getId());
        Assertions.assertEquals(result.getAuthorName(), comments.getAuthor().getName());
        Assertions.assertEquals(result.getText(), comments.getText());
        Assertions.assertEquals(result.getCreated(), comments.getCreated());
    }

    @Test
    public void toCommentTest() {
        ItemMapper itemMapper1 = new ItemMapper();
        CommentDto inputCommentDto = new CommentDto(1L, "comment1", 1L, "name", LocalDateTime.now());
        Comments result = itemMapper1.toComment(inputCommentDto, user, item);
        Assertions.assertEquals(result.getId(), inputCommentDto.getId());
        Assertions.assertEquals(result.getText(), inputCommentDto.getText());
        Assertions.assertEquals(result.getItem(), item);
        Assertions.assertEquals(result.getAuthor().getId(), inputCommentDto.getAuthor());
        Assertions.assertEquals(result.getCreated().getDayOfMonth(), LocalDateTime.now().getDayOfMonth());
    }


}
