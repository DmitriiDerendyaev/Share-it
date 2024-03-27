package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto create(UserDto userDto);

    public UserDto update(UserDto userDto, Long userId);

    public List<UserDto> getAll();

    public UserDto getById(Long userId);

    public void deleteById(Long id);
}