package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    @DirtiesContext
    void findAllTest() {
        User user = new User(0L, "userName", "email@mail.ru");
        User savedUser = new User(1L, "userName", "email@mail.ru");
        userRepository.save(user);
        List<User> check = userRepository.findAll();
        assertNotNull(check);
        check.get(0).setId(1L);
        assertEquals(check.get(0).getId(), savedUser.getId());
        assertEquals(check.get(0).getName(), savedUser.getName());
    }
}