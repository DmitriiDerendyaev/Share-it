package ru.practicum.shareit.user;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {

    User create(User user);

    User update(User user, Long userId);

    User getById(Long userId);

    List<User> getAll();

    User deleteById(Long userId);

    boolean containsUser(Long userId);

    boolean existsByEmail(String email);

    boolean existByEmailAndId(User user, Long id);
}
