package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @DirtiesContext
    public void searchTest() {
        String text = "name";
        User user2 = new User(0L, "name2", "mail2@mail.ru");
        userRepository.save(user2);
        user2.setId(3L);
        Item item = new Item(0L, "name", "desc", true, user2, null);
        itemRepository.save(item);
        ItemDto itemDto = new ItemDto(1L, "name", "desc", true, 0L);
        List<Item> result = itemRepository.search(text);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getName(), text);
    }
}