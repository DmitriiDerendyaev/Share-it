package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;

    User user = new User(1L, "userName", "email@mail.ru");
    User user2 = new User(2L, "userName2", "email2@mail.ru");
    Item item = new Item(1L, "itemName", "itemDescription", true, user, null);
    Item item1 = new Item(2L, "itemName2", "itemDescription2", true, user, null);
    LocalDateTime start = LocalDateTime.of(2024, Month.APRIL, 8, 12, 30);
    LocalDateTime end = LocalDateTime.of(2024, Month.APRIL, 12, 12, 30);
    Item itemAvailableFalse = new Item(3L, "itemName", "itemDescription", false, user, null);
    BookingItemDto bookingDtoItemAvailableFalse = new BookingItemDto(1L, 3L, start, end, BookingStatus.APPROVED);
    Booking booking = new Booking(1L, start, end, item, user, BookingStatus.APPROVED);
    Booking saveBooking = new Booking(1L, start, end, item, user2, BookingStatus.WAITING);
    BookingItemDto bookingDto = new BookingItemDto(0L, 1L, LocalDateTime.of(2024, Month.APRIL, 8, 12, 30),
            LocalDateTime.of(2024, Month.APRIL, 12, 12, 30), BookingStatus.WAITING);

    BookingItemDto bookingDtoOutput = new BookingItemDto(1L, 1L, LocalDateTime.of(2024, Month.APRIL, 8, 12, 30),
            LocalDateTime.of(2024, Month.APRIL, 12, 12, 30), BookingStatus.WAITING);
    BookingItemDto bookingDtoTime = new BookingItemDto(0L, 1L, LocalDateTime.of(2024, Month.APRIL, 8, 12, 30),
            LocalDateTime.of(2023, Month.APRIL, 12, 12, 30), BookingStatus.APPROVED);
    BookingItemDto bookingDtoEqualTime = new BookingItemDto(0L, 1L, LocalDateTime.of(2023, Month.APRIL, 12, 12, 30),
            LocalDateTime.of(2023, Month.APRIL, 12, 12, 30), BookingStatus.APPROVED);

    @Test
    public void createBookingTest() {

        Booking baseBooking = new Booking();
        baseBooking.setId(0L);
        baseBooking.setItem(item);
        baseBooking.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        baseBooking.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        baseBooking.setBooker(user2);
        baseBooking.setStatus(BookingStatus.WAITING);

        Booking baseBooking1 = new Booking();
        baseBooking1.setId(1L);
        baseBooking1.setItem(item);
        baseBooking1.setStart(LocalDateTime.of(2024, Month.APRIL, 8, 12, 30));
        baseBooking1.setEnd(LocalDateTime.of(2024, Month.APRIL, 12, 12, 30));
        baseBooking1.setBooker(user2);
        baseBooking1.setStatus(BookingStatus.WAITING);

        when(userRepository.existsById(2L)).thenReturn(true);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        when(bookingRepository.save(baseBooking)).thenReturn(baseBooking1);

        Booking result = bookingService.createBooking(2L, bookingDto);
        Assertions.assertEquals(baseBooking1.getId(), result.getId());
    }

    @Test
    public void createBookingEndBeforeStartTimeTest() {
        final ValidException exception = Assertions.assertThrows(
                ValidException.class,
                () -> bookingService.createBooking(user.getId(), bookingDtoTime));

        Assertions.assertEquals("Data start can't be later than end", exception.getMessage());
    }

    @Test
    public void createBookingEqualTimeTest() {
        final ValidException exception = Assertions.assertThrows(
                ValidException.class,
                () -> bookingService.createBooking(user.getId(), bookingDtoEqualTime));

        Assertions.assertEquals("Dates start and end can be different", exception.getMessage());
    }

    @Test
    public void createBookingNotFoundUserTest() {
        when(userRepository.existsById(user.getId())).thenReturn(false);

        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.createBooking(user.getId(), bookingDto));

        Assertions.assertEquals("This user is not exist", exception.getMessage());
    }

    @Test
    public void createBookingNotExistItemTest() {
        when(userRepository.existsById(user.getId())).thenReturn(true);

        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.createBooking(user.getId(), bookingDto));

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    public void createBookingEmptyItemTest() {
        when(itemRepository.findById(item.getId()).isEmpty()).thenThrow(new ObjectNotFoundException("Item not found"));

        final ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> itemRepository.findById(item.getId()).isEmpty());

        Assertions.assertEquals("Item not found", exception.getMessage());
    }

    @Test
    public void getBookingTest() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(saveBooking));

        Booking result = bookingService.getBooking(1L, 1L);
        Assertions.assertEquals(saveBooking, result);
    }

    @Test
    public void getBookingsByStatusTest() {
        List<Booking> bookingsByUserId = new ArrayList<>();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerId(1L, PageRequest.of(0, 1,
                Sort.by("start").descending()))).thenReturn(bookingsByUserId);

        List<Booking> allBookings = bookingService.checkState(bookingsByUserId, "ALL");

        List<Booking> result = bookingService.getBookingsByStatus(1L, "ALL", PageRequest.of(0, 1,
                Sort.by("start").descending()));

        Assertions.assertEquals(allBookings, result);
    }

    @Test
    public void getUserBookingsTest() {
        List<Item> itemByOwnerId = List.of(item, item1);

        when(itemRepository.findByOwnerId(1L, null)).thenReturn(itemByOwnerId);
        List<Booking> saveBooking = new ArrayList<>();
        List<Long> allItemsByUser = List.of(1L, 2L);

        when(bookingRepository.findByItemIdIn(allItemsByUser, PageRequest.of(0, 1,
                Sort.by("start").descending()))).thenReturn(saveBooking);

        List<Booking> allBooking = bookingService.checkState(saveBooking, "ALL");
        List<Booking> result = bookingService.getUserBookings(1L, "ALL", PageRequest.of(0, 1,
                Sort.by("start").descending()));

        Assertions.assertEquals(allBooking, result);
    }

    @Test
    public void checkRequestTest() {
        String approved = "true";
        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(saveBooking));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        Booking result = bookingService.checkRequest(1L, 1L, approved);

        Assertions.assertEquals(saveBooking, result);
    }

    @Test
    public void checkRequestApprovedIsBlankTest() {
        String approved = "";
        ValidException exception = Assertions.assertThrows(
                ValidException.class,
                () -> bookingService.checkRequest(user.getId(), booking.getId(), approved));
        Assertions.assertEquals("approved must be true/false", exception.getMessage());
    }

    @Test
    public void checkBookNotFoundTest() {
        when(bookingRepository.existsById(1L)).thenReturn(false);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(saveBooking));
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.checkRequest(user.getId(), booking.getId(), "true"));
        Assertions.assertEquals("This booking not found", exception.getMessage());
    }

    @Test
    public void checkUserNotFoundTest() {
        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(saveBooking));
        when(userRepository.existsById(1L)).thenReturn(false);
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.checkRequest(user.getId(), booking.getId(), "true"));
        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void checkOwnerChangeStatusTest() {
        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(saveBooking));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);
        Item anotherOwner = item;
        anotherOwner.setOwner(User.builder().id(123L).build());
        when(itemRepository.getReferenceById(anyLong())).thenReturn(anotherOwner);

        ObjectNotFoundException exception = Assertions.assertThrows(
                ObjectNotFoundException.class,
                () -> bookingService.checkRequest(user.getId(), booking.getId(), "true"));
        Assertions.assertEquals("Change status can only owner", exception.getMessage());
    }

    @Test
    public void checkBookingStatusTest() {
        when(bookingRepository.existsById(1L)).thenReturn(true);
        Booking approvedBooking = saveBooking;
        saveBooking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(approvedBooking));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        ValidException exception = Assertions.assertThrows(
                ValidException.class,
                () -> bookingService.checkRequest(user.getId(), booking.getId(), "true"));
        Assertions.assertEquals("This booking checked", exception.getMessage());
    }

    @Test
    public void checkRequestNotApprovedTest() {
        String approved = "false";
        when(bookingRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(saveBooking));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);

        Booking result = bookingService.checkRequest(1L, 1L, approved);

        Booking notApprovedBooking = saveBooking;
        notApprovedBooking.setStatus(BookingStatus.REJECTED);
        Assertions.assertEquals(notApprovedBooking, result);
    }


    @Test
    public void checkStateTest() {
        User user = new User(1L, "userName", "email@mail.ru");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);
        Booking bookingCURRENT = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(2), item, user, BookingStatus.APPROVED);
        Booking bookingPAST = new Booking(2L, LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(4), item, user, BookingStatus.APPROVED);
        Booking bookingFUTURE = new Booking(3L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(5), item, user, BookingStatus.APPROVED);
        Booking bookingWAITING = new Booking(4L, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusDays(4), item, user, BookingStatus.WAITING); //current
        Booking bookingREJECTED = new Booking(5L, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1), item, user, BookingStatus.REJECTED); //past
        List<Booking> allBooking = List.of(bookingCURRENT, bookingFUTURE, bookingREJECTED, bookingPAST, bookingWAITING);

        List<Booking> resultCURRENT = bookingService.checkState(allBooking, "CURRENT");
        Assertions.assertEquals(resultCURRENT.size(), 2);

        List<Booking> resultPAST = bookingService.checkState(allBooking, "PAST");
        Assertions.assertEquals(resultPAST.size(), 2);

        List<Booking> resultFUTURE = bookingService.checkState(allBooking, "FUTURE");
        Assertions.assertEquals(resultFUTURE.get(0).getId(), bookingFUTURE.getId());

        List<Booking> resultWAITING = bookingService.checkState(allBooking, "WAITING");
        Assertions.assertEquals(resultWAITING.get(0).getId(), bookingWAITING.getId());

        List<Booking> resultREJECTED = bookingService.checkState(allBooking, "REJECTED");
        Assertions.assertEquals(resultREJECTED.get(0).getId(), bookingREJECTED.getId());
    }

    @Test
    public void toBookingTest() {
        when(bookingMapper.toBooking(bookingDto, user, item)).thenReturn(booking);

        Booking bookingResult = bookingMapper.toBooking(bookingDto, user, item);

        Assertions.assertEquals(bookingDtoOutput.getId(), bookingResult.getId());
        Assertions.assertEquals(bookingDtoOutput.getItemId(), bookingResult.getItem().getId());
    }


}
