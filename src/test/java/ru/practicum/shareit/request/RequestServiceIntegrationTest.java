package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.model.Comments;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class RequestServiceIntegrationTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestMapper itemRequestMapper;
    @Autowired
    private ItemRequestServiceImpl itemRequestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Transactional
    public void getAllRequestsTest() {
        User user = new User(0L, "userName", "user@user.ru");
        User baseUser = userRepository.save(user);

        User user2 = User.builder()
                .id(0L)
                .name("Нико")
                .email("nik7@mail.ru")
                .build();
        User baseUser2 = userRepository.save(user2);

        Item item = Item.builder()
                .id(0L)
                .name("item")
                .description("description item")
                .available(true)
                .owner(baseUser)
                .request(null)
                .build();
        Item item1 = itemRepository.save(item);

        Booking baseBooking = new Booking();
        baseBooking.setId(0L);
        baseBooking.setStart(LocalDateTime.of(2023, Month.APRIL, 8, 12, 30));
        baseBooking.setEnd(LocalDateTime.of(2023, Month.APRIL, 10, 12, 30));
        baseBooking.setBooker(baseUser2);
        baseBooking.setItem(item1);
        baseBooking.setStatus(BookingStatus.APPROVED);

        Comments comments = new Comments();
        comments.setText("srzgezb");
        comments.setCreated(LocalDateTime.of(2023, Month.APRIL, 11, 12, 30));
        comments.setItem(item1);
        comments.setAuthor(baseUser);

        bookingRepository.save(baseBooking);
        commentRepository.save(comments);

        List<ItemRequestDto> allRequesrs = itemRequestRepository.findAll(PageRequest.of(0, 1, Sort.by("created").descending())).stream()
                .filter(x -> !Objects.equals(x.getRequester().getId(), baseUser2.getId()))
                .map(x -> itemRequestMapper.toDtoRequest(x, itemRepository.findByRequestId(x.getId())))
                .collect(Collectors.toList());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(baseUser.getId(), PageRequest.of(0, 1, Sort.by("created").descending()));

        Assertions.assertEquals(allRequesrs, result);
    }
}