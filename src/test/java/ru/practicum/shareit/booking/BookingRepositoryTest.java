package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;


import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Transactional
    public void findByBookerIdTest() {

        User user = new User(0L, "name", "mail@mail.ru");
        User user2 = new User(0L, "name2", "mail2@mail.ru");
        userRepository.save(user);
        userRepository.save(user2);
        user.setId(1L);
        user2.setId(2L);
        Item item = new Item(0L, "name", "desc", true, user2, null);
        itemRepository.save(item);

        Booking booking = new Booking(0L, LocalDateTime.of(2023, Month.APRIL, 8, 12, 30),
                LocalDateTime.of(2023, Month.APRIL, 10, 12, 30), item, user, BookingStatus.APPROVED);
        bookingRepository.save(booking);

        List<Booking> result = bookingRepository.findByBookerId(user.getId(), PageRequest.of(0, 1,
                Sort.by("start").descending()));

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getBooker().getId(), user.getId());
    }
}