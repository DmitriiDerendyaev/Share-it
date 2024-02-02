package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void getUsersIntegrationTest() {

        User user = new User(0L, "userName", "user@user.ru");
        User userNew = userRepository.save(user);
        UserDto userDto = userMapper.toDto(userNew);

        List<UserDto> allUsers = userRepository.findAll()
                .stream()
                .map(x -> userMapper.toDto(x))
                .collect(Collectors.toList());

        Assertions.assertEquals(userDto.getId(), allUsers.get(0).getId());
        Assertions.assertEquals(userDto.getName(), allUsers.get(0).getName());
        Assertions.assertEquals(userDto.getEmail(), allUsers.get(0).getEmail());
    }
}