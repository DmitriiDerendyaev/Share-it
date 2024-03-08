package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.model.Comments;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.comment.dto.CommentDto;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestService itemRequestService;

    private final UserService userService;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(itemDto, "itemDto must not be null");

        checkUser(userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Item item = itemMapper.toItem(itemDto,  null, userMapper.toDto(user));
        if (itemDto.getRequestId() != null) {
            itemRequestRepository.findById(itemDto.getRequestId())
                    .ifPresent(item::setRequest);
        }


        if (item.getAvailable() == null) {
            log.error("Available can't be empty");
            throw new ValidException("Available can't be empty");
        }

        if (item.getName().isBlank()) {
            log.error("Name can't be empty");
            throw new ValidException("Name can't be empty");
        }

        if (item.getDescription() == null) {
            log.error("Description can't be empty");
            throw new ValidException("Description can't be empty");
        }

        if (!userRepository.existsById(userId)) {
            log.error("This user is not exist");
            throw new ObjectNotFoundException("This user is not exist");
        }

        return itemMapper.toItemDto(itemRepository.save(item));
    }

        @Override
        public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
            Objects.requireNonNull(userId, "userId must not be null");
            Objects.requireNonNull(itemDto, "itemDto must not be null");
            Objects.requireNonNull(itemId, "itemId must not be null");

            ItemRequest itemRequest = itemRequestService.findRequestByIdUtil(itemDto.getRequestId(), userId);
            UserDto userDto = userService.getById(userId);
            Item item = itemMapper.toItem(itemDto, itemRequest, userDto);

            checkUser(userId);

            if (!itemRepository.existsById(itemId)) {
                throw new ObjectNotFoundException("This item not found");
            }

            Item savedItem = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
            if (!savedItem.getOwner().getId().equals(userId)) {
                throw new ObjectNotFoundException("Items can only be changed by owners");
            }

            if (item.getAvailable() != null) {
                savedItem.setAvailable(item.getAvailable());
            }

            if (item.getName() != null) {
                savedItem.setName(item.getName());
            }

            if (item.getDescription() != null) {
                savedItem.setDescription(item.getDescription());
            }

            return itemMapper.toItemDto(itemRepository.save(savedItem));
        }

    @Override
    public ItemDtoForOwners findById(Long itemId, Long userId) {
        Objects.requireNonNull(itemId, "itemId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        if (!itemRepository.existsById(itemId)) {
            throw new ObjectNotFoundException("Item not found");
        }

        List<Booking> saveBookings = bookingRepository.findByItemIdAndStatus(itemId, BookingStatus.APPROVED);

        Booking lastBooking = saveBookings.stream()
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now()) ||
                        ((x.getStart().isBefore(LocalDateTime.now())) && (x.getEnd().isAfter(LocalDateTime.now()))))
                .max((Comparator.comparing(Booking::getEnd))).orElse(null);

        Booking nextBooking = saveBookings.stream()
                .filter(x -> x.getStart().isAfter(LocalDateTime.now()))
                .min((Comparator.comparing(Booking::getStart))).orElse(null);

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(x -> CommentDto.builder()
                        .id(x.getId())
                        .author(x.getAuthor().getId())
                        .authorName(x.getAuthor().getName())
                        .text(x.getText())
                        .created(x.getCreated())
                        .build())
                .collect(Collectors.toList());

        return itemMapper.toItemDtoForOwners(itemRepository.getReferenceById(itemId), userId, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemDtoForOwners> getItemsByUserId(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        checkUser(userId);

        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User not found");
        }

        return itemRepository.findByOwnerId(userId).stream()
                .map(x -> findById(x.getId(), userId))
                .sorted(Comparator.comparing(ItemDtoForOwners::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(itemId, "itemDto must not be null");
        Objects.requireNonNull(commentDto, "commentDto must not be null");

        if (commentDto.getText().isBlank()) {
            throw new ValidException("This field can't be empty, write the text");
        }

        List<Booking> bookingsItemByUser = bookingRepository.findByBookerIdAndItemId(userId, itemId);

        if (bookingsItemByUser.isEmpty()) {
            throw new ObjectNotFoundException("You can't write the comment, because you didn't booking this item");
        }

        List<Booking> bookingsEndsBeforeNow = bookingsItemByUser.stream()
                .filter(x -> x.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        if (bookingsEndsBeforeNow.isEmpty()) {
            throw new ValidException("You can't comment, because you didn't use this item");
        }

        Comments comments = new Comments();
        comments.setAuthor(userRepository.getReferenceById(userId));
        comments.setText(commentDto.getText());
        comments.setItem(itemRepository.getReferenceById(itemId));
        comments.setCreated(LocalDateTime.now());

        return itemMapper.toCommentDto(commentRepository.save(comments));
    }

    public void checkUser(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        if (userId == 0) {
            log.warn("User id can't be empty");
            throw new ValidException("User id can't be empty");
        }
    }
}