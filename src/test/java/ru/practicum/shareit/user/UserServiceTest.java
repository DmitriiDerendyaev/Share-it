package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelExtensionsKt;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.times;

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
            .id(1L)
            .name("Alex")
            .email("alex@mail.ru")
            .build();

    public UserDto userInputDto = UserDto.builder()
            .id(null)
            .name("Alex")
            .email("alex@mail.ru")
            .build();

    public UserDto userOutputDto = UserDto.builder()
            .id(1L)
            .name("Alex")
            .email("alex@mail.ru")
            .build();

    public UserDto newNameUser = UserDto.builder()
            .id(1L)
            .name("Sasha")
            .email("alex@mail.ru")
            .build();

    public User updateNewNameUser = User.builder()
            .id(1L)
            .name("Sasha")
            .email("alex@mail.ru")
            .build();

    public UserDto newNameUserDto = UserDto.builder()
            .id(1L)
            .name("Sasha")
            .email("alex@mail.ru")
            .build();

    public List<User> users = new ArrayList<>();

    @Test
    public void createUserTest() {
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
        Mockito.when(userRepository.save(updateNewNameUser)).thenReturn(updateNewNameUser);
        Mockito.when(userMapper.toDto(updateNewNameUser)).thenReturn(newNameUserDto);

        UserDto userDtoResult = userService.update(newNameUser, 1L);

        Assertions.assertEquals(newNameUserDto, userDtoResult);
    }

    @Test
    public void getAllUsersTest() {
        users.add(userOutput);
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.when(userMapper.toDto(userOutput)).thenReturn(userOutputDto);
        List<UserDto> userDtoList = users.stream()
                .map(x -> userMapper.toDto(x))
                .collect(Collectors.toList());

        List<UserDto> userDtoListResult = userService.getAll();

        Assertions.assertEquals(userDtoList, userDtoListResult);
    }

    @Test
    public void getUserByIdTest() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userOutput));
        Mockito.when(userMapper.toDto(userOutput)).thenReturn(userOutputDto);
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);

        UserDto gotUser = userService.getById(1L);

        Assertions.assertEquals(userOutputDto, gotUser);
    }

    @Test
    public void deleteUserByIdTest() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(userOutput));

        userService.deleteById(1L);

        Mockito.verify(userRepository, times(1)).findById(1L);
        Mockito.verify(userRepository, times(1)).deleteById(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertFalse(userRepository.findById(1L).isPresent(), "Пользователь должен быть удален");

    }
}


























