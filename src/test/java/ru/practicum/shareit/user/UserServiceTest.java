package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    public User userInput = User.builder()
            .id(null)
            .name("Alex")
            .email("alex@mail.ru")
            .build();

    public User userOutput = User.builder()
            .id(null)
            .name("Alex")
            .email("alex@mail.ru")
            .build();

    public UserDto userInputDto = UserDto.builder()
            .id(1L)
            .name("Alex")
            .email("alex@mail.ru")
            .build();

    public UserDto userOutputDto = UserDto.builder()
            .id(1L)
            .name("Alex")
            .email("alex@mail.ru")
            .build();

    @Test
    public void userCreatedTest() {
        Mockito.when(userMapper.toUser(userInputDto)).thenReturn(userInput);
        Mockito.when(userRepository.save(userInput)).thenReturn(userOutput);
        Mockito.when(userMapper.toDto(userOutput)).thenReturn(userOutputDto);

        UserDto userDtoResult = userService.create(userInputDto);

        Assertions.assertEquals(userOutputDto, userDtoResult);
    }

    @Test
    public void updateUserTest() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userOutput));
    }
}
