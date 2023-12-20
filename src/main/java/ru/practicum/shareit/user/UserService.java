package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    public UserDto create(User user);

    public UserDto update(User user, Long userId);

    public List<UserDto> getAll();

    public User getById(Long userId);

    public User deleteById(Long id);
}